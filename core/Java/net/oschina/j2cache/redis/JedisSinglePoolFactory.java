package net.oschina.j2cache.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

/**
 * @author vill on 16/1/11 09:30.
 * @desc redis 数据连接池工厂
 */
public class JedisSinglePoolFactory implements PoolFactory<Jedis> {

    private static JedisPool jedisPool;
    private RedisPoolConfig poolConfig;

    private int cacheDefaultExpire = 1000;

    public synchronized JedisPool getJedisPool() {
        return jedisPool;
    }

    @Override
    public Jedis getResource() {
        return getJedisPool().getResource();
    }

    @Override
    public void returnResource(Jedis client) {
        client.close();
    }

    public void build() {
        String host = this.poolConfig.getHost();
        int port = this.poolConfig.getPort();
        int timeout = this.poolConfig.getTimeout();
        String password = this.poolConfig.getPassword();
        if (password != null && !"".equals(password))
            jedisPool = new JedisPool(poolConfig, host, port, timeout, password);
        else jedisPool = new JedisPool(poolConfig, host, port, timeout);
    }

    public RedisPoolConfig getPoolConfig() {
        return this.poolConfig;
    }

    public void setPoolConfig(RedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
    }

    public int getCacheDefaultExpire() {
        return cacheDefaultExpire;
    }

    public void setCacheDefaultExpire(int cacheDefaultExpire) {
        this.cacheDefaultExpire = cacheDefaultExpire;
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
