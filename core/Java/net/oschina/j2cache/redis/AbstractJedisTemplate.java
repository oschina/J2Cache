package net.oschina.j2cache.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import net.oschina.j2cache.util.SerializationUtils;
import net.oschina.j2cache.util.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.util.*;

/**
 * 抽象的redis 操作模版类
 *
 * @author zhangyw
 * @date 16/11/25 09:34
 */
public abstract class AbstractJedisTemplate<C extends JedisCommands, F extends PoolFactory<C>> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected F jedisPoolFactory;

    public void setJedisPoolFactory(F jedisPoolFactory) {
        this.jedisPoolFactory = jedisPoolFactory;
    }

    public F getJedisPoolFactory() {
        return jedisPoolFactory;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        String value = null;
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = StringUtils.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.error("get {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }


    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public abstract Object getObject(String key);

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时 秒 为单位
     * @return
     */
    public String set(String key, String value, int cacheSeconds) {
        String result = null;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
            logger.error("set {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时 秒 为单位
     * @return
     */
    public abstract String setObject(String key, Object value, int cacheSeconds);

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public List<String> getList(String key) {
        List<String> value = null;
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
                logger.debug("getList {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取List缓存
     *
     * @param key 键
     * @return 值
     */
    public abstract List<Object> getObjectList(String key);

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            String[] array = value.toArray(new String[value.size()]);
            result = jedis.rpush(key, array);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setList {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setList {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置List缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public abstract long setObjectList(String key, List<Object> value, int cacheSeconds);

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public long listAdd(String key, String... value) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpush(key, value);
            logger.debug("listAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("listAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public abstract long listObjectAdd(String key, Object... value);

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Set<String> getSet(String key) {
        Set<String> value = null;
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.smembers(key);
                logger.debug("getSet {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public abstract Set<Object> getObjectSet(String key);

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            String[] array = value.toArray(new String[]{});
            result = jedis.sadd(key, array);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setSet {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSet {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Set缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public abstract long setObjectSet(String key, Set<Object> value, int cacheSeconds);

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public long setSetAdd(String key, String... value) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.sadd(key, value);
            logger.debug("setSetAdd {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setSetAdd {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public abstract long setSetObjectAdd(String key, Object... value);

    /**
     * 移除Set缓存中的值
     *
     * @param key     键
     * @param members 值
     * @return
     */
    public long removeSet(String key, String... members) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.srem(key, members);
            logger.debug("setRemove {}  {}", key, members);
        } catch (Exception e) {
            logger.warn("setRemove {}  {}", key, members, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public Map<String, String> getMap(String key) {
        Map<String, String> value = null;
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.hgetAll(key);
                logger.debug("getMap {} = {}", key, value);
            }
        } catch (Exception e) {
            logger.warn("getMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取Map缓存
     *
     * @param key 键
     * @return 值
     */
    public abstract Map<String, Object> getObjectMap(String key);

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public String setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.hmset(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("setMap {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("setMap {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Map缓存
     *
     * @param key          键
     * @param value        值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    public abstract String setObjectMap(String key, Map<String, Object> value, int cacheSeconds);

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public String mapPut(String key, Map<String, String> value) {
        String result = null;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.hmset(key, value);
            logger.debug("mapPut {} = {}", key, value);
        } catch (Exception e) {
            logger.warn("mapPut {} = {}", key, value, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Map缓存中添加值
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public abstract String mapObjectPut(String key, Map<String, Object> value);

    /**
     * 移除Map缓存中的值
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public long mapRemove(String key, String mapKey) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(key, mapKey);
            logger.debug("mapRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.warn("mapRemove {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public long mapObjectRemove(String key, String mapKey) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(key, mapKey);
            logger.debug("mapObjectRemove {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.error("mapObjectRemove {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public boolean mapExists(String key, String mapKey) {
        boolean result = false;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(key, mapKey);
            logger.debug("mapExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.error("mapExists {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     *
     * @param key    键
     * @param mapKey 值
     * @return
     */
    public boolean mapObjectExists(String key, String mapKey) {
        boolean result = false;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(key, mapKey);
            logger.debug("mapObjectExists {}  {}", key, mapKey);
        } catch (Exception e) {
            logger.error("mapObjectExists {}  {}", key, mapKey, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }


    /**
     * 把对象放入Hash中
     *
     * @param key
     * @param field
     * @param object
     */
    public void hset(String key, String field, Object object) {
        C jedis = null;
        try {
            jedis = getResource();
            String val;
            if (object instanceof String)
                val = (String) object;
            else if (object instanceof Byte[])
                val = new String((byte[]) object);
            else
                val = JSON.toJSONString(object);
            jedis.hset(key, field, val);
            logger.debug("hset {} set {} {}", key, field, object);
        } catch (Exception e) {
            logger.error("hset {} set {} {}", key, field, e);
        } finally {
            returnResource(jedis);
        }
    }


    public void hset(byte[] key, byte[] field, byte[] val) {
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                ((Jedis) jedis).hset(key, field, val);
            }
            if (jedis instanceof JedisCluster) {
                ((JedisCluster) jedis).hset(key, field, val);
            }
            if (jedis instanceof ShardedJedis) {
                ((ShardedJedis) jedis).hset(key, field, val);
            }
            logger.debug("hset {}:{} ", new String(key), new String(field));
        } catch (Exception e) {
            logger.error("hset error:{}", ExceptionUtils.getStackTrace(e));
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 从Hash中获取对象
     *
     * @param key
     * @param field
     * @return
     */
    public String hget(String key, String field) {
        C jedis = null;
        String text = null;
        try {
            jedis = getResource();
            text = jedis.hget(key, field);
            logger.debug("hget {}:{} {}", key, field, text);
        } catch (Exception e) {
            logger.error("hget error:{}", e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return text;
    }

    public byte[] hget(byte[] key, byte[] field) {
        C jedis = null;
        byte[] text = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                text = ((Jedis) jedis).hget(key, field);
            }
            if (jedis instanceof JedisCluster) {
                text = ((JedisCluster) jedis).hget(key, field);
            }
            if (jedis instanceof ShardedJedis) {
                text = ((ShardedJedis) jedis).hget(key, field);
            }
            logger.debug("hget {}:{} {}", new String(key), new String(field), text);
        } catch (Exception e) {
            logger.error("hget error:{}", ExceptionUtils.getStackTrace(e));
        } finally {
            returnResource(jedis);
        }
        return text;
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
        String text = hget(key, field);
        if (text == null) {
            return null;
        }
        return JSON.parseObject(text, clazz);
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
        String text = hget(key, field);
        if (text == null) {
            return null;
        }
        return JSON.parseObject(text, javaType);
    }

    /**
     * *从Hash中删除对象
     */
    public int hdel(String key, String... field) {
        C jedis = null;
        Long result = 0l;
        try {
            jedis = getResource();
            result = jedis.hdel(key, field);
            logger.debug("hdel {}:{}", key, field);
        } catch (Exception e) {
            logger.error("hdel error:{}", e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return result.intValue();
    }

    public int hdel(byte[] key, byte[]... field) {

        Long count = Long.valueOf(0);
        C jedis = null;
        byte[] text = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                count = ((Jedis) jedis).hdel(key, field);
            }
            if (jedis instanceof JedisCluster) {
                count = ((JedisCluster) jedis).hdel(key, field);
            }
            if (jedis instanceof ShardedJedis) {
                count = ((ShardedJedis) jedis).hdel(key, field);
            }
            logger.debug("hdel {}:{} ", new String(key), Arrays.toString(field));
        } catch (Exception e) {
            logger.error("hdel error:{}", ExceptionUtils.getStackTrace(e));
        } finally {
            returnResource(jedis);
        }

        return count.intValue();
    }

    /**
     * 获取长度
     *
     * @param key
     * @return
     */
    public int hlen(String key) {
        C jedis = null;
        Long len = 0l;
        try {
            jedis = getResource();
            len = jedis.hlen(key);
        } catch (Exception e) {
            logger.error("hlen error:{}", e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return len.intValue();
    }

    public Set<String> hkeys(String key) {
        C jedis = null;
        try {
            jedis = getResource();
            return jedis.hkeys(key);
        } catch (Exception e) {
            logger.error("hlen error:{}", e.getMessage());
        } finally {
            returnResource(jedis);
        }
        return null;

    }

    public <T> Collection<T> hvals(String key, Class<T> clazz) {
        C jedis = null;
        try {
            jedis = getResource();
            List<T> tList = new LinkedList<>();
            List<String> list = jedis.hvals(key);
            for (String string : list) {
                tList.add(JSON.parseObject(string, clazz));
            }
            return tList;
        } catch (Exception e) {
            logger.error("hvals error:{}", e.getMessage());
            return null;
        } finally {
            returnResource(jedis);
        }

    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public long del(String key) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            if (key != null) {
                result = jedis.del(key);
                logger.debug("del {}", key);
            }
        } catch (Exception e) {
            logger.warn("del {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public long del(byte[] key) {
        Long count = (long) 0;
        C jedis = null;
        byte[] text = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                count = ((Jedis) jedis).del(key);
            }
            if (jedis instanceof JedisCluster) {
                count = ((JedisCluster) jedis).del(key);
            }
            if (jedis instanceof ShardedJedis) {
                count = ((ShardedJedis) jedis).del(key);
            }
            logger.debug("del {}:{} ", new String(key));
        } catch (Exception e) {
            logger.error("del error:{}", ExceptionUtils.getStackTrace(e));
        } finally {
            returnResource(jedis);
        }

        return count;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public long del(String... key) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            if (key != null && key.length > 0) {
                for (String s : key) {
                    result += jedis.del(s);
                    logger.debug("del {}", s);
                }
            }
        } catch (Exception e) {
            logger.warn("del {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     *
     * @param key 键
     * @return
     */
    public long delObject(String key) {
        long result = 0;
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                result = jedis.del(key);
                logger.debug("delObject {}", key);
            } else {
                logger.debug("delObject {} not exists", key);
            }
        } catch (Exception e) {
            logger.warn("delObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public boolean exists(String key) {
        boolean result = false;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(key);
            logger.debug("exists {}", key);
        } catch (Exception e) {
            logger.error("exists {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     *
     * @param key 键
     * @return
     */
    public boolean existsObject(String key) {
        boolean result = false;
        C jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(key);
            logger.debug("existsObject {}", key);
        } catch (Exception e) {
            logger.error("existsObject {}", key, e);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    public void subscribe(JedisPubSub jedisPubSub, String... channels) {
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                ((Jedis) jedis).subscribe(jedisPubSub, channels);
            }
            if (jedis instanceof JedisCluster) {
                ((JedisCluster) jedis).subscribe(jedisPubSub, channels);
            }
            if (jedis instanceof ShardedJedis) {
                throw new UnsupportedOperationException("shard jedis not supported subscribe event");
            }
        } catch (Exception ignored) {
        } finally {
            returnResource(jedis);
        }
    }

    public void publish(String channel, String message) {
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                ((Jedis) jedis).publish(channel, message);
            }
            if (jedis instanceof JedisCluster) {
                ((JedisCluster) jedis).publish(channel, message);
            }
            if (jedis instanceof ShardedJedis) {
                throw new UnsupportedOperationException("shard jedis not supported publish event");
            }
        } catch (Exception ignored) {
        } finally {
            returnResource(jedis);
        }
    }

    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                ((Jedis) jedis).subscribe(jedisPubSub, channels);
            }
            if (jedis instanceof JedisCluster) {
                ((JedisCluster) jedis).subscribe(jedisPubSub, channels);
            }
            if (jedis instanceof ShardedJedis) {
                throw new UnsupportedOperationException("shard jedis not supported subscribe event");
            }
        } catch (Exception ignored) {
        } finally {
            returnResource(jedis);
        }
    }

    public void publish(byte[] channel, byte[] message) {
        C jedis = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                ((Jedis) jedis).publish(channel, message);
            }
            if (jedis instanceof JedisCluster) {
                ((JedisCluster) jedis).publish(channel, message);
            }
            if (jedis instanceof ShardedJedis) {
                throw new UnsupportedOperationException("shard jedis not supported publish event");
            }
        } catch (Exception ignored) {
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 获取资源
     *
     * @return
     * @throws JedisException
     */
    public C getResource() throws JedisException {
        C jedis = null;
        try {
            jedis = jedisPoolFactory.getResource();
//			logger.debug("getResource.", jedis);
        } catch (JedisException e) {
            logger.error("getResource error {}", e.getMessage());
            throw e;
        }
        return jedis;
    }

    /**
     * 释放资源
     *
     * @param jedis
     */
    public void returnResource(C jedis) {
        if (jedis != null) {
            jedisPoolFactory.returnResource(jedis);
        }
    }

    /**
     * 获取byte[]类型Key
     *
     * @param object
     * @return
     */
    public byte[] getBytesKey(Object object) {
        if (object instanceof String) {
            return ((String) object).getBytes();
        } else {
            return toBytes(object);
        }
    }

    /**
     * Object转换byte[]类型
     *
     * @param object
     * @return
     */
    public byte[] toBytes(Object object) {
        try {
            return SerializationUtils.serialize(object);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * byte[]型转换Object
     *
     * @param bytes
     * @return
     */
    public Object toObject(byte[] bytes) {
        try {
            return SerializationUtils.deserialize(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
