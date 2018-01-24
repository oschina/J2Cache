/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cache Channel, the J2Cache methods explored to developers
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public abstract class CacheChannel implements Closeable , AutoCloseable {

	private final static Map<String, Object> _g_keyLocks = new ConcurrentHashMap<>();

	/**
	 * <p>Just for Inner Use.</p>
	 *
	 * <p>To clear the whole region when received this event .</p>
	 *
	 * @param region Cache region name
	 */
	protected abstract void sendClearCmd(String region);

	/**
	 * <p>Just for Inner Use.</p>
	 *
	 * <p>To remove cached data when received this event .</p>
	 *
	 * @param region Cache region name
	 * @param keys	Cache data key
	 */
	protected abstract void sendEvictCmd(String region, String...keys);

	/**
	 * 读取缓存（用户无需判断返回的对象是否为空）
	 * @param region Cache region name
	 * @param key Cache data key
	 * @return cache object
	 */
	public CacheObject get(String region, String key)  {
		CacheObject obj = new CacheObject(region, key, CacheObject.LEVEL_1);
		obj.setValue(CacheProviderHolder.getLevel1Cache(region).get(key));
		if(obj.rawValue() == null) {
			obj.setLevel(CacheObject.LEVEL_2);
			obj.setValue(CacheProviderHolder.getLevel2Cache(region).get(key));
			if(obj.rawValue() != null)
				CacheProviderHolder.getLevel1Cache(region).put(key, obj.rawValue());
		}
		return obj;
	}

	/**
	 * 支持外部数据自动加载的缓存方法
	 * @param region Cache region name
	 * @param key Cache data key
	 * @param loader data loader
	 * @return cache object
	 */
	public CacheObject get(String region, String key, Function<String, Object> loader, boolean...cacheNullObject) {
		CacheObject cache = get(region, key);
		if (cache.rawValue() == null) {
			String lock_key = key + '@' + region;
			synchronized (_g_keyLocks.computeIfAbsent(lock_key, v -> new Object())) {
				cache = get(region, key);
				if (cache.rawValue() == null) {
					try {
						Object obj = loader.apply(key);
						if (obj != null) {
							boolean cacheNull = (cacheNullObject.length>0)?cacheNullObject[0]:false;
							set(region, key, obj, cacheNull);
							cache = new CacheObject(region, key, CacheObject.LEVEL_OUTER, obj);
						}
					} finally {
						_g_keyLocks.remove(lock_key);
					}
				}
			}
		}
		return cache;
	}

	/**
	 * 批量读取缓存中的对象（用户无需判断返回的对象是否为空）
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache object
	 */
	public Map<String, CacheObject> get(String region, Collection<String> keys)  {
		final Map<String, Object> objs = CacheProviderHolder.getLevel1Cache(region).get(keys);
		List<String> level2Keys = keys.stream().filter(k -> !objs.containsKey(k) || objs.get(k) == null).collect(Collectors.toList());
		Map<String, CacheObject> results = objs.entrySet().stream().filter(p -> p.getValue() != null).collect(
			Collectors.toMap(
				p -> p.getKey(),
				p -> new CacheObject(region, p.getKey(), CacheObject.LEVEL_1, p.getValue())
			)
		);

		Map<String, Object> objs_level2 = CacheProviderHolder.getLevel2Cache(region).get(level2Keys);
		objs_level2.forEach((k,v) -> {
			results.put(k, new CacheObject(region, k, CacheObject.LEVEL_2, v));
			if (v != null)
				CacheProviderHolder.getLevel1Cache(region).put(k, v);
		});

		return results;
	}

	/**
	 * 使用数据加载器的批量缓存读取
	 * @param region Cache region name
	 * @param keys cache keys
	 * @param loader data loader
	 * @return
	 */
	public Map<String, CacheObject> get(String region, Collection<String> keys, Function<String, Object> loader, boolean...cacheNullObject)  {
		Map<String, CacheObject> results = get(region, keys);
		results.entrySet().stream().filter(e -> e.getValue().rawValue() == null).forEach( e -> {
			String lock_key = e.getKey() + '@' + region;
			synchronized (_g_keyLocks.computeIfAbsent(lock_key, v -> new Object())) {
				CacheObject cache = get(region, e.getKey());
				if(cache == null) {
					try {
						Object obj = loader.apply(e.getKey());
						if (obj != null) {
							boolean cacheNull = (cacheNullObject.length>0)?cacheNullObject[0]:false;
							set(region, e.getKey(), obj, cacheNull);
							e.getValue().setValue(obj);
							e.getValue().setLevel(CacheObject.LEVEL_OUTER);
						}
					} finally {
						_g_keyLocks.remove(lock_key);
					}
				}
				else {
					e.setValue(cache);
				}
			}
		});
		return results;
	}

	/**
	 * 判断某个缓存键是否存在
	 * @param region Cache region name
	 * @param key cache key
	 * @return true if key exists
	 */
	public boolean exists(String region, String key) {
		boolean exists = CacheProviderHolder.getLevel1Cache(region).exists(key);
		if(!exists)
			exists = CacheProviderHolder.getLevel2Cache(region).exists(key);
		return exists;
	}

	/**
	 * Write data to J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @param value: Cache value
	 */
	public void set(String region, String key, Object value) {
		set(region, key, value,false);
	}

	/**
	 * Write data to J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @param value: Cache value
	 * @param cacheNullObject if allow cache null object
	 */
	public void set(String region, String key, Object value, boolean cacheNullObject) {
		try {
			CacheProviderHolder.getLevel1Cache(region).put(key, (value==null && cacheNullObject)?new Object():value);
			CacheProviderHolder.getLevel2Cache(region).put(key, (value==null && cacheNullObject)?new Object():value);
		} finally {
			this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
		}
    }


	/**
	 * Write data to j2cache with expired setting
	 *
	 * <strong>注意：强烈不推荐使用带 TTL 的 set 方法，所有的缓存 TTL 都应该预先配置好，避免多个节点的缓存 Region 配置不同步</strong>
	 *
	 * @param region Cache Region name
	 * @param key Cache Key
	 * @param value Cache value
	 * @param timeToLiveInSeconds cache expired in second
	 */
	public void set(String region, String key, Object value, long timeToLiveInSeconds ) {
		set(region, key, value, timeToLiveInSeconds, false);
	}

	/**
	 * Write data to j2cache with expired setting
	 *
	 * <strong>注意：强烈不推荐使用带 TTL 的 set 方法，所有的缓存 TTL 都应该预先配置好，避免多个节点的缓存 Region 配置不同步</strong>
	 *
	 * @param region Cache Region name
	 * @param key Cache Key
	 * @param value Cache value
	 * @param timeToLiveInSeconds cache expired in second
	 * @param cacheNullObject if allow cache null object
	 */
    public void set(String region, String key, Object value, long timeToLiveInSeconds, boolean cacheNullObject) {
    	if(timeToLiveInSeconds <= 0)
    		set(region, key, value, cacheNullObject);
    	else {
			try {
				CacheProviderHolder.getLevel1Cache(region, timeToLiveInSeconds).put(key, (value==null && cacheNullObject)?new Object():value);
				CacheProviderHolder.getLevel2Cache(region).put(key, (value==null && cacheNullObject)?new Object():value);
			} finally {
				this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
			}
		}
	}

	/**
	 * 批量插入数据
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 */
	public void set(String region, Map<String, Object> elements){
    	set(region, elements, false);
	}

	/**
	 * 批量插入数据
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @param cacheNullObject if allow cache null object
	 */
	public void set(String region, Map<String, Object> elements, boolean cacheNullObject)  {
		try {
			if (cacheNullObject && elements.containsValue(null)) {
				Map<String, Object> newElems = new HashMap<>();
				newElems.putAll(elements);
				newElems.forEach((k,v) -> {
					if (v == null)
						newElems.put(k, new Object());
				});
				CacheProviderHolder.getLevel1Cache(region).put(newElems);
				CacheProviderHolder.getLevel2Cache(region).put(newElems);
			}
			else {
				CacheProviderHolder.getLevel1Cache(region).put(elements);
				CacheProviderHolder.getLevel2Cache(region).put(elements);
			}
		} finally {
			//广播
			this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
		}
	}

	/**
	 * 带失效时间的批量缓存数据插入
	 *
	 * <strong>注意：强烈不推荐使用带 TTL 的 set 方法，所有的缓存 TTL 都应该预先配置好，避免多个节点的缓存 Region 配置不同步</strong>
	 *
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @param timeToLiveInSeconds cache expired in second
	 */
	public void set(String region, Map<String, Object> elements, long timeToLiveInSeconds){
		set(region, elements, timeToLiveInSeconds, false);
	}

	/**
	 * 带失效时间的批量缓存数据插入
	 *
	 * <strong>注意：强烈不推荐使用带 TTL 的 set 方法，所有的缓存 TTL 都应该预先配置好，避免多个节点的缓存 Region 配置不同步</strong>
	 *
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @param timeToLiveInSeconds cache expired in second
	 * @param cacheNullObject if allow cache null object
	 */
	public void set(String region, Map<String, Object> elements, long timeToLiveInSeconds, boolean cacheNullObject)  {
		if(timeToLiveInSeconds <= 0)
			set(region, elements, cacheNullObject);
		else {
			try {
				if (cacheNullObject && elements.containsValue(null)) {
					Map<String, Object> newElems = new HashMap<>();
					newElems.putAll(elements);
					newElems.forEach((k,v) -> {
						if (v == null)
							newElems.put(k, new Object());
					});
					CacheProviderHolder.getLevel1Cache(region, timeToLiveInSeconds).put(newElems);
					CacheProviderHolder.getLevel2Cache(region).put(newElems);
				}
				else {
					CacheProviderHolder.getLevel1Cache(region, timeToLiveInSeconds).put(elements);
					CacheProviderHolder.getLevel2Cache(region).put(elements);
				}
			} finally {
				//广播
				this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
			}
		}
	}

	/**
	 * Remove cached data in J2Cache
	 *
	 * @param region:  Cache Region name
	 * @param keys: Cache key
	 */
	public void evict(String region, String...keys)  {
		try {
			CacheProviderHolder.getLevel1Cache(region).evict(keys);
			CacheProviderHolder.getLevel2Cache(region).evict(keys);
		} finally {
			this.sendEvictCmd(region, keys); //发送广播
		}
    }

	/**
	 * Clear the cache
	 *
	 * @param region: Cache region name
	 */
	public void clear(String region)  {
		try {
			CacheProviderHolder.getLevel1Cache(region).clear();
			CacheProviderHolder.getLevel2Cache(region).clear();
		}finally {
			this.sendClearCmd(region);
		}
    }

	/**
	 * 返回所有的缓存区域
	 * @return
	 */
	public Collection<Region> regions() {
		return CacheProviderHolder.regions();
	}

	/**
	 * <p>Get cache region keys</p>
	 * <p><strong>Notice: ehcache3 not support keys</strong></p>
	 *
	 * @param region: Cache region name
	 * @return key list
	 */
	public Collection<String> keys(String region)  {
		Set<String> keys = new HashSet<>();
		keys.addAll(CacheProviderHolder.getLevel1Cache(region).keys());
		keys.addAll(CacheProviderHolder.getLevel2Cache(region).keys());
		return keys;
    }

	/**
	 * Close J2Cache
	 */
	public abstract void close();

	/**
	 * 获取一级缓存接口
	 * @return 返回一级缓存的 CacheProvider 实例
	 */
	public CacheProvider getL1Provider() {
		return CacheProviderHolder.getL1Provider();
	}

	/**
	 * <p>获取二级缓存的接口，该方法可用来直接获取 J2Cache 底层的 Redis 客户端实例</p>
	 * <p>方法如下：</p>
	 * <code>
	 *     CacheChannel channel = J2Cache.getChannel();
	 *     RedisClient client = ((RedisCacheProvider)channel.getL2Provider()).getRedisClient();
	 *     try {
	 *     	   client.get().xxxxx(...);
	 *     } finally {
	 *         client.release();
	 *     }
	 * </code>
	 * @return 返回二级缓存的 CacheProvider 实例
	 */
	public CacheProvider getL2Provider() {
		return CacheProviderHolder.getL2Provider();
	}

	/**
	 * Cache Region Define
	 */
	public static class Region {

		private String name;
		private long size;
		private long ttl;

		public Region(){}
		public Region(String name, long size, long ttl) {
			this.name = name;
			this.size = size;
			this.ttl = ttl;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public long getSize() {
			return size;
		}

		public void setSize(long size) {
			this.size = size;
		}

		public long getTtl() {
			return ttl;
		}

		public void setTtl(long ttl) {
			this.ttl = ttl;
		}

		@Override
		public String toString() {
			return String.format("[%s,size:%d,ttl:%d]", name, size, ttl);
		}
	}

}
