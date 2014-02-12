package net.oschina.j2cache.redis;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;

/**
 * Redis 缓存实现
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
			return b == null ? null : byte2obj(b);
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
						: obj2byte(value));
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
			Set<byte[]> list = cache.keys(String.valueOf("*").getBytes());
			if (null != list && list.size() > 0) {
				for (byte[] bs : list) {
					keys.add(bs == null ? null : byte2obj(bs));
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
			cache.expire(String.valueOf(region + ":" + key).getBytes(), 0);
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

	private byte[] obj2byte(Object obj) {
		ObjectOutputStream oos = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new CacheException(e);
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (IOException e) {
				}
		}
	}

	private Object byte2obj(byte[] bits) {
		ObjectInputStream ois = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bits);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
				}
		}
	}
}
