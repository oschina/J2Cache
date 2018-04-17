package net.oschina.j2cache.cache.support.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import net.oschina.j2cache.Level2Cache;

/**
 * 重新实现二级缓存
 * @author zhangsaizz
 *
 */
public class SpringRedisCache implements Level2Cache {

	private String namespace;

	private String region;

	private RedisTemplate<String, Serializable> redisTemplate;

	public SpringRedisCache(String namespace, String region, RedisTemplate<String, Serializable> redisTemplate) {
		if (region == null || region.isEmpty()) {
			region = "_"; // 缺省region
		}
		this.namespace = namespace;
		this.redisTemplate = redisTemplate;
		this.region = getRegionName(region);
	}

	private String getRegionName(String region) {
		if (namespace != null && !namespace.isEmpty())
			region = namespace + ":" + region;
		return region;
	}

	@Override
	public void clear() {
		redisTemplate.opsForHash().delete(region);
	}

//	@Override
//	public Serializable get(String key) {
//		Object value = redisTemplate.opsForHash().get(region, key);
//		if (value == null) {
//			return null;
//		}
//		return (Serializable) bs;
//	}

//	@Override
//	public Map<String, Object> get(Collection<String> keys) {
//		Map<String, Object> map = new HashMap<>(keys.size());
//		for (String k : keys) {
//			Object value = redisTemplate.opsForHash().get(region, k);
//			if (value != null) {
//				map.put(k, (Serializable) value);
//			} else {
//				map.put(k, null);
//			}
//		}
//		return map;
//	}

	@Override
	public boolean exists(String key) {
		return redisTemplate.opsForHash().hasKey(region, key);
	}

//	@Override
//	public void put(String key, Object value) {
//		redisTemplate.opsForHash().put(region, key, value);
//	}

//	@Override
//	public void put(Map<String, Object> elements) {
//		Map<String, Object> map = new HashMap<>(elements.size());
//		elements.forEach((k, v) -> {
//			map.put(k, v);
//		});
//		redisTemplate.opsForHash().putAll(region, map);
//	}

	@Override
	public void evict(String... keys) {
		for (String k : keys) {
			if(!k.equals("null")) {
				redisTemplate.opsForHash().delete(region, k);
			}else {
				redisTemplate.delete(region);
			}
		}
	}

	@Override
	public Collection<String> keys() {
		Set<Object> list = redisTemplate.opsForHash().keys(region);
		List<String> keys = new ArrayList<>(list.size());
		for (Object object : list) {
			keys.add((String) object);
		}
		return keys;
	}

	@Override
	public byte[] getBytes(String key) {
		return redisTemplate.opsForHash().getOperations().execute(new RedisCallback<byte[]>() {
			public byte[] doInRedis(RedisConnection redis) {
				return redis.hGet(region.getBytes(), key.getBytes());
			}
		});
	}

	@Override
	public List<byte[]> getBytes(Collection<String> keys) {
		return redisTemplate.opsForHash().getOperations().execute(new RedisCallback<List<byte[]>>() {
			@Override
			public List<byte[]> doInRedis(RedisConnection redis) throws DataAccessException {
				byte[][] bytes = keys.stream().map(k -> k.getBytes()).toArray(byte[][]::new);
				return redis.hMGet(region.getBytes(), bytes);
			}
		});
	}

	@Override
	public void setBytes(Map<String, byte[]> bytes) {
		redisTemplate.opsForHash().putAll(region, bytes);
	}

	@Override
	public void setBytes(String key, byte[] bytes) {
		redisTemplate.opsForHash().put(region, key, bytes);
	}
}
