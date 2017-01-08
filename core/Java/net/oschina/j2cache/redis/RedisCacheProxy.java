package net.oschina.j2cache.redis;

import com.alibaba.fastjson.TypeReference;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.JedisPubSub;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * redis client proxy，
 * provide support for redis cluster client, single client, shard client
 *
 * @author zhangyw
 * @version 17/1/8 11:44
 */
public class RedisCacheProxy implements Closeable{

    private AbstractJedisTemplate jedisTemplate;

    private RedisCacheProxy() {

    }

    public RedisCacheProxy(JedisHandlerAdapter jedisHandlerAdapter) {
        this.jedisTemplate = jedisHandlerAdapter.getJedisTemplate();
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public Map<String, String> getMap(String key) {
        return jedisTemplate.getMap(key);
    }

    public Set<String> hkeys(String key) {
        return jedisTemplate.hkeys(key);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Set<Object> getObjectSet(String key) {
        return jedisTemplate.getObjectSet(key);
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public boolean exists(String key) {
        return jedisTemplate.exists(key);
    }

    public int hdel(byte[] key, byte[]... field) {
        return jedisTemplate.hdel(key, field);
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public String mapPut(String key, Map value) {
        return jedisTemplate.mapPut(key, value);
    }

    public long del(byte[] key) {
        return jedisTemplate.del(key);
    }
    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public long del(String key) {
        return jedisTemplate.del(key);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public long del(String... key) {
        return jedisTemplate.del(key);
    }

    /**
     * 从Hash中获取对象,转换成制定类型
     *
     * @param key
     * @param field
     * @param javaType
     * @return
     */
    public <T> T hget(String key, String field, TypeReference<T> javaType) {
        return (T) jedisTemplate.hget(key, field, javaType);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return jedisTemplate.get(key);
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setMap(String key, Map value, int cacheSeconds) {
        return jedisTemplate.setMap(key, value, cacheSeconds);
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时 秒 为单位
     * @return
     */
    public String set(String key, String value, int cacheSeconds) {
        return jedisTemplate.set(key, value, cacheSeconds);
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public long setSetAdd(String key, String... value) {
        return jedisTemplate.setSetAdd(key, value);
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时 秒 为单位
     * @return
     */
    public String setObject(String key, Object value, int cacheSeconds) {
        return jedisTemplate.setObject(key, value, cacheSeconds);
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setObjectSet(String key, Set value, int cacheSeconds) {
        return jedisTemplate.setObjectSet(key, value, cacheSeconds);
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public List<Object> getObjectList(String key) {
        return jedisTemplate.getObjectList(key);
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public boolean mapObjectExists(String key, String mapKey) {
        return jedisTemplate.mapObjectExists(key, mapKey);
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public long listAdd(String key, String... value) {
        return jedisTemplate.listAdd(key, value);
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public boolean mapExists(String key, String mapKey) {
        return jedisTemplate.mapExists(key, mapKey);
    }

    /**
     * 把对象放入Hash中
     *
     * @param key
     * @param field
     * @param object
     */
    public void hset(String key, String field, Object object) {
        jedisTemplate.hset(key, field, object);
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public long listObjectAdd(String key, Object... value) {
        return jedisTemplate.listObjectAdd(key, value);
    }

    public void publish(byte[] channel, byte[] message) {
        jedisTemplate.publish(channel, message);
    }

    /**
     * 从Hash中获取对象,转换成制定类型
     *
     * @param key
     * @param field
     * @param clazz
     * @return
     */
    public <T> T hget(String key, String field, Class<T> clazz) {
        return (T) jedisTemplate.hget(key, field, clazz);
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public boolean existsObject(String key) {
        return jedisTemplate.existsObject(key);
    }

    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
        jedisTemplate.subscribe(jedisPubSub, channels);
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public String mapObjectPut(String key, Map value) {
        return jedisTemplate.mapObjectPut(key, value);
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public List<String> getList(String key) {
        return jedisTemplate.getList(key);
    }

    /**
     * 移除Set缓存中的值
     *
     * @param key     键
     * @param members 值
     * @return
     */
    public long removeSet(String key, String... members) {
        return jedisTemplate.removeSet(key, members);
    }

    /**
     * 获取长度
     *
     * @param key
     * @return
     */
    public int hlen(String key) {
        return jedisTemplate.hlen(key);
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setObjectList(String key, List value, int cacheSeconds) {
        return jedisTemplate.setObjectList(key, value, cacheSeconds);
    }

    public void publish(String channel, String message) {
        jedisTemplate.publish(channel, message);
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public long delObject(String key) {
        return jedisTemplate.delObject(key);
    }

    /**
     * *从Hash中删除对象
     *
     * @param key
     * @param field
     */
    public int hdel(String key, String... field) {
        return jedisTemplate.hdel(key, field);
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setList(String key, List value, int cacheSeconds) {
        return jedisTemplate.setList(key, value, cacheSeconds);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object getObject(String key) {
        return jedisTemplate.getObject(key);
    }

    /**
     * 从Hash中获取对象
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        return jedisTemplate.hget(key, field);
    }

    public void hset(byte[] key, byte[] field, byte[] val) {
        jedisTemplate.hset(key, field, val);
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public long mapObjectRemove(String key, String mapKey) {
        return jedisTemplate.mapObjectRemove(key, mapKey);
    }

    public byte[] hget(byte[] key, byte[] field) {
        return jedisTemplate.hget(key, field);
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public Map<String, Object> getObjectMap(String key) {
        return jedisTemplate.getObjectMap(key);
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setObjectMap(String key, Map value, int cacheSeconds) {
        return jedisTemplate.setObjectMap(key, value, cacheSeconds);
    }

    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        jedisTemplate.subscribe(jedisPubSub, channels);
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Set<String> getSet(String key) {
        return jedisTemplate.getSet(key);
    }

    public <T> Collection<T> hvals(String key, Class clazz) {
        return jedisTemplate.hvals(key, clazz);
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setSet(String key, Set value, int cacheSeconds) {
        return jedisTemplate.setSet(key, value, cacheSeconds);
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public long setSetObjectAdd(String key, Object... value) {
        return jedisTemplate.setSetObjectAdd(key, value);
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public long mapRemove(String key, String mapKey) {
        return jedisTemplate.mapRemove(key, mapKey);
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
        jedisTemplate.getJedisPoolFactory().close();
    }

}
