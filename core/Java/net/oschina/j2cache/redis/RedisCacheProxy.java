package net.oschina.j2cache.redis;

import net.oschina.j2cache.redis.client.RedisClient;
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

    private RedisClientFactoryAdapter redisClientFactoryAdapter;

    public RedisCacheProxy(RedisClientFactoryAdapter redisClientFactoryAdapter) {
        this.redisClientFactoryAdapter = redisClientFactoryAdapter;
        if (this.redisClientFactoryAdapter == null) {
            throw new RuntimeException("jedis handler adapter must configuration");
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

    public void close() {
        redisClientFactoryAdapter.close();
    }
}
