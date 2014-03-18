package net.oschina.j2cache.redis;

import java.util.List;

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

	private String region;

	public RedisCache(String region) {
		this.region = region;
	}
	
	/**
	 * 生成缓存的 key
	 * @param key
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private String getKeyName(Object key) {

		if(key instanceof Number)
			return region + ":I:" + key;
		else{
			Class keyClass = key.getClass();
			if(String.class.equals(keyClass) || StringBuffer.class.equals(keyClass) || StringBuilder.class.equals(keyClass))
				return region + ":S:" + key;
		}
		return region + ":O:" + key;
	}
	
	public static void main(String[] args) {
		RedisCache cache = new RedisCache("User");
		System.out.println(cache.getKeyName("Hello"));
		System.out.println(cache.getKeyName(2));
		System.out.println(cache.getKeyName((byte)2));
		System.out.println(cache.getKeyName(2L));
	}

	@Override
	public Object get(Object key) throws CacheException {
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			if (null == key)
				return null;
			byte[] b = cache.get(getKeyName(key).getBytes());
			return b == null ? null : SerializationUtils.deserialize(b);
		} catch (Exception e) {
			broken = true;
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(cache, broken);
		}
	}

	@Override
	public void put(Object key, Object value) throws CacheException {
		if (value == null)
			evict(key);
		else {
			boolean broken = false;
			Jedis cache = RedisCacheProvider.getResource();
			try {
				cache.set(getKeyName(key).getBytes(), SerializationUtils.serialize(value));
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
			cache.del(getKeyName(key));
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
				okeys[i] = getKeyName(keys.get(i));
			}
			cache.del(okeys);
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
		throw new CacheException("Operation not supported.");
	}

	@Override
	public void clear() throws CacheException {
		throw new CacheException("Operation not supported.");
	}

	@Override
	public void destroy() throws CacheException {
		this.clear();
	}
}
