package net.oschina.j2cache.redis;

import net.oschina.j2cache.redis.client.RedisClient;
import net.oschina.j2cache.redis.support.BlockSupportConfig;
import net.oschina.j2cache.redis.support.RedisClientFactoryAdapter;
import redis.clients.jedis.BinaryJedisPubSub;

import java.io.Closeable;
import java.util.Set;

/**
 * Redis cache 代理，用来获取 redis client
 *
 * @author zhangyw
 */
public class RedisCacheProxy implements Closeable {
    private int timeOutMillis = 60000; //加锁超时时间
    private int timeLockMillis = 60000;//加锁锁定时间
    private int stripes = 1024;//默认锁的个数
    private int timeWaitMillis = 300;//加锁过程每次恢复时间
    private boolean block = true;//是否阻塞

    private RedisClientFactoryAdapter redisClientFactoryAdapter;


    public RedisCacheProxy(RedisClientFactoryAdapter redisClientFactoryAdapter) {
        this.redisClientFactoryAdapter = redisClientFactoryAdapter;
        if (this.redisClientFactoryAdapter == null) {
            throw new RuntimeException("jedis handler adapter must configuration");
        }
    }

    public RedisCacheProxy(BlockSupportConfig config, RedisClientFactoryAdapter redisClientFactoryAdapter) {
        this(redisClientFactoryAdapter);
        if (config != null) {
            setBlock(config.isBlock());
            setStripes(config.getStripes());
            setTimeLockMillis(config.getTimeLockMillis());
            setTimeWaitMillis(config.getTimeWaitMillis());
            setTimeOutMillis(config.getTimeOutMillis());
        }

    }


    public RedisClient getResource() {
        return this.redisClientFactoryAdapter.getRedisClientFactory().getResource();
    }

    public void returnResource(RedisClient redisClient) {
        this.redisClientFactoryAdapter.getRedisClientFactory().returnResource(redisClient);
    }

    public byte[] hget(byte[] key, byte[] fieldKey) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            return redisClient.hget(key, fieldKey);
        } finally {
            returnResource(redisClient);
        }
    }


    public void hset(byte[] key, byte[] fieldKey, byte[] val) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            redisClient.hset(key, fieldKey, val);
        } finally {
            returnResource(redisClient);
        }
    }


    public void hset(byte[] key, byte[] fieldKey, byte[] val, int expireInSec) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            redisClient.hset(key, fieldKey, val);
            redisClient.expire(key, expireInSec);
        } finally {
            returnResource(redisClient);
        }
    }

    public void hdel(byte[] key, byte[]... fieldKey) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            redisClient.hdel(key, fieldKey);
        } finally {
            returnResource(redisClient);
        }
    }

    public Set<String> hkeys(String key) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            return redisClient.hkeys(key);
        } finally {
            returnResource(redisClient);
        }
    }

    public Set<byte[]> hkeys(byte[] key) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            return redisClient.hkeys(key);
        } finally {
            returnResource(redisClient);
        }
    }

    public void del(String key) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            redisClient.del(key);
        } finally {
            returnResource(redisClient);
        }
    }

    public void del(byte[] key) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            redisClient.del(key);
        } finally {
            returnResource(redisClient);
        }
    }

    public void subscribe(BinaryJedisPubSub binaryJedisPubSub, byte[]... channels) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            redisClient.subscribe(binaryJedisPubSub, channels);
        } finally {
            returnResource(redisClient);
        }
    }

    public void publish(byte[] channel, byte[] message) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            redisClient.publish(channel, message);
        } finally {
            returnResource(redisClient);
        }
    }

    public String set(String key, String value, String nxxx, String expx, long time) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            return redisClient.set(key, value, nxxx, expx, time);
        } finally {
            returnResource(redisClient);
        }

    }

    public String set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            return redisClient.set(key, value, nxxx, expx, time);
        } finally {
            returnResource(redisClient);
        }
    }

    public Long del(byte[]... keys) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            return redisClient.del(keys);
        } finally {
            returnResource(redisClient);
        }
    }

    public Set<byte[]> keys(byte[] pattern) {
        RedisClient redisClient = null;
        try {
            redisClient = getResource();
            return redisClient.keys(pattern);
        } finally {
            returnResource(redisClient);
        }
    }


    public void close() {
        redisClientFactoryAdapter.close();
    }

    public int getTimeOutMillis() {
        return timeOutMillis;
    }

    public void setTimeOutMillis(int timeOutMillis) {
        this.timeOutMillis = timeOutMillis;
    }

    public int getTimeLockMillis() {
        return timeLockMillis;
    }

    public void setTimeLockMillis(int timeLockMillis) {
        this.timeLockMillis = timeLockMillis;
    }

    public int getStripes() {
        return stripes;
    }

    public void setStripes(int stripes) {
        this.stripes = stripes;
    }

    public int getTimeWaitMillis() {
        return timeWaitMillis;
    }

    public void setTimeWaitMillis(int timeWaitMillis) {
        this.timeWaitMillis = timeWaitMillis;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }
}
