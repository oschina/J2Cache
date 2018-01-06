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
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Cache Channel, the J2Cache methods explored to developers
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public abstract class CacheChannel implements Closeable {

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
	 * Get CacheObject from J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @return cache object
	 * @throws IOException io exception
	 */
	public CacheObject get(String region, String key) throws IOException {
		CacheObject obj = new CacheObject(region, key, CacheProviderHolder.LEVEL_1);
		obj.setValue(CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key));
		if(obj.getValue() == null) {
			obj.setValue(CacheProviderHolder.get(CacheProviderHolder.LEVEL_2, region, key));
			if(obj.getValue() != null){
				obj.setLevel(CacheProviderHolder.LEVEL_2);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj.getValue());
			}
		}
		return obj;
	}

	/**
	 * Get User Original Object from J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @return	User object
	 * @throws IOException io exception
	 */
	public Serializable getRawObject(String region, String key) throws IOException {
		CacheObject cache = get(region, key);
		return (cache != null)?cache.getValue():null;
	}

	/**
	 * 批量获取缓存对象
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache objects
	 * @throws IOException io exception
	 */
	public Map<String, CacheObject> getAll(String region, Collection<String> keys) throws IOException {
		Map<String, CacheObject> values = new HashMap<>();
		List<String> keys_not_in_level_1 = new ArrayList<>();
		for(String key : keys){
			Serializable obj = CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key);
			if(obj != null) {
				CacheObject cacheObject = new CacheObject(region, key, CacheProviderHolder.LEVEL_1, obj);
				values.put(key, cacheObject);
			}
			else
				keys_not_in_level_1.add(key);
		}
		for(String key : keys_not_in_level_1) {
			Serializable obj = CacheProviderHolder.get(CacheProviderHolder.LEVEL_2, region, key);
			if(obj != null) {
				CacheObject cacheObject = new CacheObject(region, key, CacheProviderHolder.LEVEL_2, obj);
				values.put(key, cacheObject);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj);
			}
		}
		return values;
	}

	/**
	 * 判断某个缓存键是否存在
	 * @param region Cache region name
	 * @param key cache key
	 * @return true if key exists
	 * @throws IOException io exception
	 */
	public boolean exists(String region, String key) throws IOException {
		boolean exists = CacheProviderHolder.exists(CacheProviderHolder.LEVEL_1, region, key);
		if(!exists)
			exists = CacheProviderHolder.exists(CacheProviderHolder.LEVEL_2, region, key);
		return exists;
	}

	/**
	 * Write data to J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @param value: Cache value
	 * @throws IOException io exception
	 */
	public void set(String region, String key, Serializable value) throws IOException {
		if(value == null)
			evict(region, key);
		else{
			CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, value);
			CacheProviderHolder.set(CacheProviderHolder.LEVEL_2, region, key, value);
			this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
		}
    }

	/**
	 * Write data to j2cache with expired setting
	 * @param region Cache Region name
	 * @param key Cache Key
	 * @param value Cache value
	 * @param timeToLiveInSeconds cache expired in second
 	 * @throws IOException io exception
	 */
    public void set(String region, String key, Serializable value, long timeToLiveInSeconds) throws IOException {
		if(value == null)
			evict(region, key);
		else{
			CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, value, timeToLiveInSeconds);
			CacheProviderHolder.set(CacheProviderHolder.LEVEL_2, region, key, value, timeToLiveInSeconds);
			this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
		}
	}

	/**
	 * Put an element in the cache if no element is currently mapped to the elements key.
	 * @param region Cache Region name
	 * @param key Cache key
	 * @param value Cache value
	 * @throws IOException io exception
	 */
    public void setIfAbsent(String region, String key, Serializable value) throws IOException {
    	CacheProviderHolder.setIfAbsent(CacheProviderHolder.LEVEL_1, region, key, value);
		CacheProviderHolder.setIfAbsent(CacheProviderHolder.LEVEL_2, region, key, value);
		this.sendEvictCmd(region, key);
	}

	/**
	 * Put an element in the cache if no element is currently mapped to the elements key.
	 * @param region Cache Region name
	 * @param key Cache key
	 * @param value Cache value
	 * @param timeToLiveInSeconds cache expired in second
	 * @throws IOException io exception
	 */
	public void setIfAbsent(String region, String key, Serializable value, long timeToLiveInSeconds) throws IOException {
		CacheProviderHolder.setIfAbsent(CacheProviderHolder.LEVEL_1, region, key, value, timeToLiveInSeconds);
		CacheProviderHolder.setIfAbsent(CacheProviderHolder.LEVEL_2, region, key, value, timeToLiveInSeconds);
		this.sendEvictCmd(region, key);
	}

	/**
	 * 批量插入数据
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @throws IOException io exception
	 */
	public void setAll(String region, Map<String, Serializable> elements) throws IOException {
		CacheProviderHolder.setAll(CacheProviderHolder.LEVEL_1, region, elements);
		CacheProviderHolder.setAll(CacheProviderHolder.LEVEL_2, region, elements);
		//广播
		this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
	}

	/**
	 * 带失效时间的批量缓存数据插入
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 * @param timeToLiveInSeconds cache expired in second
	 * @throws IOException io exception
	 */
	public void setAll(String region, Map<String, Serializable> elements, long timeToLiveInSeconds) throws IOException {
		CacheProviderHolder.setAll(CacheProviderHolder.LEVEL_1, region, elements, timeToLiveInSeconds);
		CacheProviderHolder.setAll(CacheProviderHolder.LEVEL_2, region, elements, timeToLiveInSeconds);
		//广播
		this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
	}

	/**
	 * Remove cached data in J2Cache
	 *
	 * @param region:  Cache Region name
	 * @param keys: Cache key
	 * @throws IOException io exception
	 */
	public void evict(String region, String...keys) throws IOException {
        CacheProviderHolder.evict(CacheProviderHolder.LEVEL_1, region, keys); //删除一级缓存
        CacheProviderHolder.evict(CacheProviderHolder.LEVEL_2, region, keys); //删除二级缓存
        this.sendEvictCmd(region, keys); //发送广播
    }

	/**
	 * Clear the cache
	 *
	 * @param region: Cache region name
	 * @throws IOException io exception
	 */
	public void clear(String region) throws IOException {
        CacheProviderHolder.clear(CacheProviderHolder.LEVEL_1, region);
        CacheProviderHolder.clear(CacheProviderHolder.LEVEL_2, region);
		this.sendClearCmd(region);
    }
	
	/**
	 * <p>Get cache region keys</p>
	 * <p><strong>Notice: ehcache3 not support keys</strong></p>
	 *
	 * @param region: Cache region name
	 * @return key list
	 * @throws IOException io exception
	 */
	public Collection<String> keys(String region) throws IOException {
        return CacheProviderHolder.keys(CacheProviderHolder.LEVEL_1, region);
    }

	/**
	 * Close J2Cache
	 */
	public abstract void close();

}
