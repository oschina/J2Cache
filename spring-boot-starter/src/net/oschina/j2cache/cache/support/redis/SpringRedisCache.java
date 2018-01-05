package net.oschina.j2cache.cache.support.redis;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;

import net.oschina.j2cache.Cache;

/**
 * 
 * @author zhangsaizz
 *
 */
public class SpringRedisCache implements Cache {

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
	public void clear() throws IOException {
		redisTemplate.opsForHash().delete(region);
	}

	@Override
	public Serializable get(String key) throws IOException {
		Object value = redisTemplate.opsForHash().get(region, getKeyName(key));
		if (value == null) {
			return null;
		}
		return (Serializable) value;
	}

	@Override
	public Map<String, Serializable> getAll(Collection<String> keys) throws IOException {
		Map<String, Serializable> map = new HashMap<>(keys.size());
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
	public boolean exists(String key) throws IOException {
		return redisTemplate.opsForHash().hasKey(region, getKeyName(key));
	}

	@Override
	public void put(String key, Serializable value) throws IOException {
		redisTemplate.opsForHash().put(region, getKeyName(key), value);
	}

	@Override
	public Serializable putIfAbsent(String key, Serializable value) throws IOException {
		if (!redisTemplate.opsForHash().putIfAbsent(region, getKeyName(key), value)) {
			return null;
		} else {
			Object o = redisTemplate.opsForHash().get(region, getKeyName(key));
			if (o == null) {
				return null;
			}
			return (Serializable) o;
		}

	}

	@Override
	public void putAll(Map<String, Serializable> elements) throws IOException {
		Map<String, Serializable> map = new HashMap<>(elements.size());
		elements.forEach((k, v) -> {
			map.put(getKeyName(k), v);
		});
		redisTemplate.opsForHash().putAll(region, map);
	}

	@Override
	public void evict(String... keys) throws IOException {
		redisTemplate.opsForHash().delete(region, keys);
	}

	@Override
	public Collection<String> keys() throws IOException {
		Set<Object> list = redisTemplate.opsForHash().keys(region);
		List<String> keys = new ArrayList<>(list.size());
		for (Object object : list) {
			keys.add((String) object);
		}
		return keys;
	}

}
