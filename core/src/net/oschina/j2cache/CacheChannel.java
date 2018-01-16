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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Cache Channel, the J2Cache methods explored to developers
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public abstract class CacheChannel implements Closeable , AutoCloseable {

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
	 * @param region
	 * @param key
	 * @return
	 */
	public CacheObject get(String region, String key)  {
		CacheObject obj = new CacheObject(region, key, CacheObject.LEVEL_1);
		obj.setValue(CacheProviderHolder.getLevel1Cache(region).get(key));
		if(obj.getValue() == null) {
			obj.setLevel(CacheObject.LEVEL_2);
			obj.setValue(CacheProviderHolder.getLevel2Cache(region).get(key));
			if(obj.getValue() != null)
				CacheProviderHolder.getLevel1Cache(region).put(key, obj.getValue());
		}
		return obj;
	}

	/**
	 * 批量读取缓存中的对象（用户无需判断返回的对象是否为空）
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache object
	 */
	public Map<String, CacheObject> get(String region, Collection<String> keys)  {
		return keys.stream().collect(Collectors.toMap(Function.identity(), key -> get(region, key)));
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
	public void set(String region, String key, Object value)  {
		if(value == null)
			evict(region, key);
		else{
			try {
				CacheProviderHolder.getLevel1Cache(region).put(key, value);
				CacheProviderHolder.getLevel2Cache(region).put(key, value);
			} finally {
				this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
			}
		}
    }

	/**
	 * Write data to j2cache with expired setting
	 * @param region Cache Region name
	 * @param key Cache Key
	 * @param value Cache value
	 * @param timeToLiveInSeconds cache expired in second
	 */
    public void set(String region, String key, Object value, long timeToLiveInSeconds)  {
    	if(timeToLiveInSeconds <= 0)
    		set(region, key, value);
    	else {
			if (value == null)
				evict(region, key);
			else {
				try {
					CacheProviderHolder.getLevel1Cache(region, timeToLiveInSeconds).put(key, value);
					CacheProviderHolder.getLevel2Cache(region).put(key, value);
				} finally {
					this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
				}
			}
		}
	}

	/**
	 * 批量插入数据
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 */
	public void set(String region, Map<String, Object> elements)  {
		try {
			CacheProviderHolder.getLevel1Cache(region).put(elements);
			CacheProviderHolder.getLevel2Cache(region).put(elements);
		} finally {
			//广播
			this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
		}
	}

	/**
	 * 带失效时间的批量缓存数据插入
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @param timeToLiveInSeconds cache expired in second
	 */
	public void set(String region, Map<String, Object> elements, long timeToLiveInSeconds)  {
		if(timeToLiveInSeconds <= 0)
			set(region, elements);
		else {
			try {
				CacheProviderHolder.getLevel1Cache(region, timeToLiveInSeconds).put(elements);
				CacheProviderHolder.getLevel2Cache(region).put(elements);
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

}
