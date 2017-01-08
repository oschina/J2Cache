package net.oschina.j2cache.redis;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author vill on 16/1/11 09:30.
 * @desc redis 数据连接池工厂
 */
public class ShardedJedisPoolFactory implements PoolFactory<ShardedJedis> {

    private static ShardedJedisPool jedisPool;
    private RedisPoolConfig poolConfig;

    private int cacheDefaultExpire = 1000;

    private List<JedisShardInfo> jedisShardInfoList;

    public synchronized ShardedJedisPool getJedisPool() {
        return jedisPool;
    }

    @Override
    public ShardedJedis getResource() {
        return getJedisPool().getResource();
    }

    @Override
    public void returnResource(ShardedJedis client) {
        client.close();
    }

    public void build() {

//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setMinIdle(minIdle);
//        jedisPoolConfig.setMaxIdle(maxIdle);
//        jedisPoolConfig.setMaxTotal(maxTotal);
//        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
//        jedisPoolConfig.setTestOnReturn(true);
        //Idle时进行连接扫描
//        jedisPoolConfig.setTestWhileIdle(true);
        //表示idle object evitor两次扫描之间要sleep的毫秒数
//        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        //表示idle object evitor每次扫描的最多的对象数
//        jedisPoolConfig.setNumTestsPerEvictionRun(10);
        //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；
        // 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
//        jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);
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

    public int getCacheDefaultExpire() {
        return cacheDefaultExpire;
    }

    public void setCacheDefaultExpire(int cacheDefaultExpire) {
        this.cacheDefaultExpire = cacheDefaultExpire;
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
