package net.oschina.j2cache.redis.support;

import net.oschina.j2cache.redis.client.SingleRedisClient;
import redis.clients.jedis.JedisPool;

import java.io.IOException;

/**
 * @author vill on 16/1/11 09:30.
 * redis 数据连接池工厂
 */
public class RedisSingleFactory implements RedisClientFactory<SingleRedisClient> {

    private static JedisPool jedisPool;
    private RedisPoolConfig poolConfig;

    public synchronized JedisPool getJedisPool() {
        return jedisPool;
    }

    @Override
    public SingleRedisClient getResource() {
        return new SingleRedisClient(getJedisPool().getResource());
    }

    @Override
    public void returnResource(SingleRedisClient client) {
        if (client != null)
            client.close();
    }

    public void build() {
        String host = this.poolConfig.getHost();
        int port = this.poolConfig.getPort();
        int timeout = this.poolConfig.getTimeout();
        int database = this.poolConfig.getDatabase();
        String password = this.poolConfig.getPassword();
        if (password != null && !"".equals(password))
            jedisPool = new JedisPool(poolConfig, host, port, timeout, password, database);
        else {
        	jedisPool = new JedisPool(poolConfig, host, port, timeout, null, database);
        }
    }

    public RedisPoolConfig getPoolConfig() {
        return this.poolConfig;
    }

    public void setPoolConfig(RedisPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
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
