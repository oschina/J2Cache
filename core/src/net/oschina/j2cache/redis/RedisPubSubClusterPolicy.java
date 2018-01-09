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
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Properties;

/**
 * 使用 Redis 的订阅和发布进行集群中的节点通知
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class RedisPubSubClusterPolicy extends BinaryJedisPubSub implements ClusterPolicy {

    private final static Logger log = LoggerFactory.getLogger(RedisPubSubClusterPolicy.class);

    private RedisClient redis;
    private String channel;
    private byte[] channelBytes;

    public RedisPubSubClusterPolicy(String channel, RedisClient redis){
        this.redis = redis;
        this.channel = channel;
        this.channelBytes = channel.getBytes();
    }

    /**
     * 加入 Redis 的发布订阅频道
     */
    @Override
    public void connect(Properties props) {
        long ct = System.currentTimeMillis();
        this.redis.publish(channelBytes, Command.join().jsonBytes());   //Join Cluster
        new Thread(()-> {
            //当 Redis 重启会导致订阅线程断开连接，需要进行重连
            while(true) {
                try {
                    redis.subscribe(this, channelBytes);
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
        redis.publish(channelBytes, Command.quit().jsonBytes()); //Quit Cluster
        this.unsubscribe();
    }

    /**
     * 发送清除缓存的广播命令
     *
     * @param region : Cache region name
     * @param keys    : cache key
     */
    @Override
    public void sendEvictCmd(String region, String...keys) {
        // 发送广播
        Command cmd = new Command(Command.OPT_EVICT_KEY, region, keys);
        try {
            redis.publish(channelBytes, cmd.jsonBytes());
        } catch (Exception e) {
            log.error("Failed to delete cache,region=" + region + ",key=" + String.join(",", keys), e);
        }
    }

    /**
     * 发送清除缓存的广播命令
     *
     * @param region: Cache region name
     */
    @Override
    public void sendClearCmd(String region) {
        // 发送广播
        Command cmd = new Command(Command.OPT_CLEAR_KEY, region, "");
        try {
            redis.publish(channelBytes, cmd.jsonBytes());
        } catch (Exception e) {
            log.error("Failed to clear cache,region=" + region, e);
        }
    }

    /**
     * 当接收到订阅频道获得的消息时触发此方法
     * @param channel 频道名称
     * @param message 消息体
     */
    public void onMessage(byte[] channel, byte[] message) {
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
