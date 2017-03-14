package net.oschina.j2cache.redis.support;

import net.oschina.j2cache.redis.client.ClusterRedisClient;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author vill on 16/1/11 09:30.
 * redis 数据连接池工厂
 */
public class RedisClusterFactory implements RedisClientFactory<ClusterRedisClient> {

    private static ClusterRedisClient redisClient;
    private RedisPoolConfig poolConfig;

    private int maxRedirections = 0;

    private Pattern p = Pattern.compile("^.+[:]\\d{1,5}\\s*$");

    @Override
    public synchronized ClusterRedisClient getResource() {
        return redisClient;
    }

    /**
     * 集群模式下 jedisCluster 不用手动返还 jedis client
     * jedisCluster 有自动返还client策略
     *
     * @param client
     */
    @Override
    public void returnResource(ClusterRedisClient client) {

    }

    public void build() {

        /**
         poolConfig.setTestOnReturn(true);
         //Idle时进行连接扫描
         poolConfig.setTestWhileIdle(true);
         //表示idle object evitor两次扫描之间要sleep的毫秒数
         poolConfig.setTimeBetweenEvictionRunsMillis(30000);
         //表示idle object evitor每次扫描的最多的对象数
         poolConfig.setNumTestsPerEvictionRun(10);
         //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；
         // 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
         poolConfig.setMinEvictableIdleTimeMillis(60000);
         **/


        Set<HostAndPort> hostAndPorts = parseHostAndPort();
        if (maxRedirections == 0) { // 最大重定向次数,这里取 node节点数量
            maxRedirections = hostAndPorts.size();
        }

        redisClient = new ClusterRedisClient(
                new JedisCluster(hostAndPorts, poolConfig.getTimeout(), maxRedirections, poolConfig));
    }

    private Set<HostAndPort> parseHostAndPort() {

        String host = this.poolConfig.getHost();

        Set<HostAndPort> haps = new HashSet<HostAndPort>();
        String[] hosts = host.split(",");
        for (String val : hosts) {
            boolean isIpPort = p.matcher(val).matches();

            if (!isIpPort) {
                throw new IllegalArgumentException("ip or port is illegal.");
            }
            String[] ipAndPort = val.split(":");
            HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
            haps.add(hap);
        }
        return haps;
    }

    public void setMaxRedirections(int maxRedirections) {
        this.maxRedirections = maxRedirections;
    }

    public void setPoolConfig(RedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public RedisPoolConfig getPoolConfig() {
        return this.poolConfig;
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        redisClient.close();
    }
}
