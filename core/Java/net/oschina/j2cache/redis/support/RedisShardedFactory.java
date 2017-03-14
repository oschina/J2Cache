package net.oschina.j2cache.redis.support;

import net.oschina.j2cache.redis.client.ShardedRedisClient;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author vill on 16/1/11 09:30.
 * redis 数据连接池工厂
 */
public class RedisShardedFactory implements RedisClientFactory<ShardedRedisClient> {

    private static ShardedJedisPool jedisPool;
    private RedisPoolConfig poolConfig;

    private List<JedisShardInfo> jedisShardInfoList;

    public synchronized ShardedJedisPool getJedisPool() {
        return jedisPool;
    }

    @Override
    public ShardedRedisClient getResource() {
        return new ShardedRedisClient(getJedisPool().getResource());
    }

    @Override
    public void returnResource(ShardedRedisClient client) {
        if (client != null)
            client.close();
    }

    public void build() {
        // redis uri 格式
        // redis://password@127.0.0.1:6379/0  多个用逗号分割
        String host = this.poolConfig.getHost();
        int timeout = this.poolConfig.getTimeout();
        if (host != null) {
            List<String> list = Arrays.asList(host.split(","));
            jedisShardInfoList = new ArrayList<>();
            for (String uri : list) {
                JedisShardInfo jedisShardInfo = new JedisShardInfo(uri);
                jedisShardInfo.setConnectionTimeout(timeout);
                jedisShardInfoList.add(jedisShardInfo);
            }
        }

        jedisPool = new ShardedJedisPool(poolConfig, jedisShardInfoList);
    }

    public RedisPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public void setPoolConfig(RedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public List<JedisShardInfo> getJedisShardInfoList() {
        return jedisShardInfoList;
    }

    public void setJedisShardInfoList(List<JedisShardInfo> jedisShardInfoList) {
        this.jedisShardInfoList = jedisShardInfoList;
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
        jedisPool.close();
    }
}
