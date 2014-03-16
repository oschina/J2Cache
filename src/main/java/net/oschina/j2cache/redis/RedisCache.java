package net.oschina.j2cache.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	@Override
	public Object get(Object key) throws CacheException {
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			if (null == key)
				return null;
			byte[] b = cache.get((region + ":" + key).getBytes());
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
			remove(key);
		else {
			boolean broken = false;
			Jedis cache = RedisCacheProvider.getResource();
			try {
				cache.set((region + ":" + key).getBytes(), value == null ? null
						: SerializationUtils.serialize(value));
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
	@SuppressWarnings("rawtypes")
	public List keys() throws CacheException {
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			List<Object> keys = new ArrayList<Object>();
			Set<byte[]> list = cache.keys(String.valueOf(region + ":" + "*")
					.getBytes());
			if (null != list && list.size() > 0) {
				for (byte[] bs : list) {
					if (null == bs)
						continue;
					String key = new String(bs);
					key = key.replaceFirst(region + ":", "");
					keys.add(key);
				}
			}
			return keys;
		} catch (Exception e) {
			broken = true;
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(cache, broken);
		}
	}

	private void remove(Object key, boolean batch) throws CacheException {
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			cache.del(region + ":" + key);
		} catch (Exception e) {
			broken = true;
			throw new CacheException(e);
		} finally {
			if (!batch)
				RedisCacheProvider.returnResource(cache, broken);
		}
	}

	@Override
	public void remove(Object key) throws CacheException {
		remove(key, false);
	}

	/* (non-Javadoc)
	 * @see net.oschina.j2cache.Cache#batchRemove(java.util.List)
	 */
	@Override
	public void batchRemove(List<String> keys) throws CacheException {
		if(keys == null || keys.size() == 0)
			return ;
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			String[] okeys = new String[keys.size()];
			keys.toArray(okeys);
			for(int i=0;i<okeys.length;i++){
				okeys[i] = region + ':' + okeys[i];
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
	public void clear() throws CacheException {
		boolean broken = false;
		Jedis cache = RedisCacheProvider.getResource();
		try {
			List keys = this.keys();
			for (Object key : keys) {
				this.remove(key, true);
			}
		} catch (Exception e) {
			broken = true;
			throw new CacheException(e);
		} finally {
			RedisCacheProvider.returnResource(cache, broken);
		}
	}

	@Override
	public void destroy() throws CacheException {
		this.clear();
	}
}
