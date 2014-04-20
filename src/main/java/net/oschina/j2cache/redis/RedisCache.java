package net.oschina.j2cache.redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.util.SerializationUtils;

/**
 * Redis 缓存实现
 * 
 * @author winterlau
 */
public class RedisCache implements Cache {

	private final static Logger log = LoggerFactory.getLogger(RedisCache.class);
	private String region;

	public RedisCache(String region) {
		this.region = region;
	}
	
	/**
	 * 生成缓存的 key（避免类似字符串 "123" 和数值 123 的重叠
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String genKeyName(Object key) {
		if(key instanceof Number)
			return "N:" + key;
		else{
			Class keyClass = key.getClass();
			if(String.class.equals(keyClass) || StringBuffer.class.equals(keyClass) || StringBuilder.class.equals(keyClass))
				return key.toString();
		}
		return "O:" + key;
	}
	
	public static void main(String[] args) {
		RedisCache cache = new RedisCache("User");
		System.out.println(cache.genKeyName("Hello"));
		System.out.println(cache.genKeyName(2));
		System.out.println(cache.genKeyName((byte)2));
		System.out.println(cache.genKeyName(2L));
	}

	@Override
	public Object get(Object key) throws CacheException {
		Object obj = null;
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			if (null == key)
				return null;
			byte[] b = cache.hget(region.getBytes(), genKeyName(key).getBytes());
			if(b != null)
				obj = SerializationUtils.deserialize(b);
		} catch (Exception e) {
			log.error("Error occured when get data from L2 cache", e);
			broken = true;
			if(e instanceof IOException)
				evict(key);
		} finally {
			RedisCacheProvider.returnResource(cache, broken);
		}
		return obj;
	}

	@Override
	public void put(Object key, Object value) throws CacheException {
		if (value == null)
			evict(key);
		else {
			boolean broken = false;
			Jedis cache = RedisCacheProvider.getResource();
			try {
				cache.hset(region.getBytes(), genKeyName(key).getBytes(), SerializationUtils.serialize(value));
			} catch (Exception e) {
				broken = true;
				throw new CacheException(e);
			} finally {
				RedisCacheProvider.returnResource(cache, broken);
			}
		}
	}

	@Override
	public void update(Object key, Object value) throws CacheException {
		put(key, value);
	}

	@Override
	public void evict(Object key) throws CacheException {
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			cache.hdel(region.getBytes(), genKeyName(key).getBytes());
		} catch (Exception e) {
			broken = true;
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(cache, broken);
		}
	}

	/* (non-Javadoc)
	 * @see net.oschina.j2cache.Cache#batchRemove(java.util.List)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void evict(List keys) throws CacheException {
		if(keys == null || keys.size() == 0)
			return ;
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			String[] okeys = new String[keys.size()];
			for(int i=0;i<okeys.length;i++){
				okeys[i] = genKeyName(keys.get(i));
			}
			cache.hdel(region, okeys);
		} catch (Exception e) {
			broken = true;
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(cache, broken);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List keys() throws CacheException {
		Jedis cache = RedisCacheProvider.getResource();
		boolean broken = false;
		try {
			List<String> keys = new ArrayList<String>();
			keys.addAll(cache.hkeys(region));
			return keys;
		} catch (Exception e) {
			broken = true;
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(cache, broken);
		}
	}

	@Override
	public void clear() throws CacheException {
		Jedis cache = RedisCacheProvider.getResource();
		boolean broken = false;
		try {
			cache.del(region.getBytes());
		} catch (Exception e) {
			broken = true;
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(cache, broken);
		}
	}

	@Override
	public void destroy() throws CacheException {
	}
}
