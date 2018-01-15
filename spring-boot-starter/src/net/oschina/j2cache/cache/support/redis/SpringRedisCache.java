package net.oschina.j2cache.cache.support.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.oschina.j2cache.Level2Cache;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
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
		if (namespace != null && !namespace.isEmpty()) {
			region = namespace + ":" + region;
		}
		return region;
	}

	private String getKeyName(Object key) {
		if (key instanceof Number)
			return "I:" + key;
		else if (key instanceof String || key instanceof StringBuilder || key instanceof StringBuffer)
			return "S:" + key;
		return "O:" + key;
	}

	@Override
	public void clear() {
		redisTemplate.opsForHash().delete(region);
	}

	@Override
	public Object get(String key) {
		Object value = redisTemplate.opsForHash().get(region, getKeyName(key));
		if (value == null) {
			return null;
		}
		return (Serializable) value;
	}

	@Override
	public Map<String, Object> get(Collection<String> keys) {
		Map<String, Object> map = new HashMap<>(keys.size());
		for (String k : keys) {
			Object value = redisTemplate.opsForHash().get(region, getKeyName(k));
			if (value != null) {
				map.put(k, (Serializable) value);
			} else {
				map.put(k, null);
			}
		}
		return map;
	}

	@Override
	public boolean exists(String key) {
		return redisTemplate.opsForHash().hasKey(region, getKeyName(key));
	}

	@Override
	public void put(String key, Object value) {
		redisTemplate.opsForHash().put(region, getKeyName(key), value);
	}

	@Override
	public void put(Map<String, Object> elements) {
		Map<String, Object> map = new HashMap<>(elements.size());
		elements.forEach((k, v) -> {
			map.put(getKeyName(k), v);
		});
		redisTemplate.opsForHash().putAll(region, map);
	}

	@Override
	public void evict(String... keys) {
		redisTemplate.opsForHash().delete(region, keys);
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
	public Long incr(String key, long l) {
		return redisTemplate.opsForHash().increment(region, key, l);
	}

	@Override
	public Long decr(String key, long l) {
		return redisTemplate.opsForHash().delete(region, key);
	}

	@Override
	public byte[] getBytes(String key) {
		return (byte[]) redisTemplate.opsForHash().get(region, key);
	}

	@Override
	public void setBytes(String key, byte[] bytes) {
		redisTemplate.opsForHash().put(region, key, bytes);
	}
}
