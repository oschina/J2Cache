/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.redis;

import net.oschina.j2cache.ClusterPolicy;
import net.oschina.j2cache.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Properties;

/**
 * 使用 Redis 的订阅和发布进行集群中的节点通知
 * 该策略器使用 j2cache.properties 中的 redis 配置自行保持两个到 redis 的连接用于发布和订阅消息（并在失败时自动重连）
 * @author Winter Lau(javayou@gmail.com)
 */
public class RedisPubSubClusterPolicy extends JedisPubSub implements ClusterPolicy {

    private final static Logger log = LoggerFactory.getLogger(RedisPubSubClusterPolicy.class);

    private JedisPool client;
    private String channel;

    private String host;
    private int port;
    private int timeout;
    private String password;

    public RedisPubSubClusterPolicy(String channel, Properties props){
        this.channel = channel;
        String node = props.getProperty("channel.host");
        if(node == null || node.trim().length() == 0)
            node = props.getProperty("hosts").split(",")[0];
        String[] infos = node.split(":");
        this.host = infos[0];
        this.port = (infos.length > 1)?Integer.parseInt(infos[1]):6379;
        this.timeout = Integer.parseInt((String)props.getOrDefault("timeout", "2000"));
        this.password = props.getProperty("password");
        if(this.password != null && this.password.trim().length() == 0)
            this.password = null;

        JedisPoolConfig config = RedisUtils.newPoolConfig(props, null);
        this.client = new JedisPool(config, host, port, timeout, password);
    }

    /**
     * 加入 Redis 的发布订阅频道
     */
    @Override
    public void connect(Properties props) {
        long ct = System.currentTimeMillis();

        try (Jedis jedis = client.getResource()) {
            jedis.publish(channel, Command.join().json());   //Join Cluster
        }

        new Thread(()-> {
            //当 Redis 重启会导致订阅线程断开连接，需要进行重连
            while(true) {
                try (Jedis jedis = client.getResource()){
                    jedis.subscribe(this, channel);
                    log.info("Disconnect to redis channel:" + channel);
                    break;
                } catch (JedisConnectionException e) {
                    log.error("Failed connect to redis, reconnect it.", e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie){
                        break;
                    }
                }
            }
        }, "RedisSubscribeThread").start();
        log.info("Connected to redis channel:" + channel + ", time " + (System.currentTimeMillis()-ct) + " ms.");
    }

    /**
     * 退出 Redis 发布订阅频道
     */
    @Override
    public void disconnect() {
        try (Jedis jedis = client.getResource()) {
            this.unsubscribe();
            jedis.publish(channel, Command.quit().json()); //Quit Cluster
        }
    }

    /**
     * 发送清除缓存的广播命令
     *
     * @param region : Cache region name
     * @param keys    : cache key
     */
    @Override
    public void sendEvictCmd(String region, String...keys) {
        try (Jedis jedis = client.getResource()) {
            jedis.publish(channel, new Command(Command.OPT_EVICT_KEY, region, keys).json());
        }
    }

    /**
     * 发送清除缓存的广播命令
     *
     * @param region: Cache region name
     */
    @Override
    public void sendClearCmd(String region) {
        try (Jedis jedis = client.getResource()) {
            jedis.publish(channel, new Command(Command.OPT_CLEAR_KEY, region, "").json());
        }
    }

    /**
     * 当接收到订阅频道获得的消息时触发此方法
     * @param channel 频道名称
     * @param message 消息体
     */
    @Override
    public void onMessage(String channel, String message) {
        try {
            Command cmd = Command.parse(message);

            if (cmd == null || cmd.isLocal())
                return;

            switch (cmd.getOperator()) {
                case Command.OPT_JOIN:
                    log.info("Node-"+cmd.getSrc()+" joined to " + this.channel);
                    break;
                case Command.OPT_EVICT_KEY:
                    this.evict(cmd.getRegion(), cmd.getKeys());
                    log.debug("Received cache evict message, region=" + cmd.getRegion() + ",key=" + String.join(",", cmd.getKeys()));
                    break;
                case Command.OPT_CLEAR_KEY:
                    this.clear(cmd.getRegion());
                    log.debug("Received cache clear message, region=" + cmd.getRegion());
                    break;
                case Command.OPT_QUIT:
                    log.info("Node-"+cmd.getSrc()+" quit to " + this.channel);
                    break;
                default:
                    log.warn("Unknown message type = " + cmd.getOperator());
            }
        } catch (Exception e) {
            log.error("Failed to handle received msg", e);
        }
    }

}
