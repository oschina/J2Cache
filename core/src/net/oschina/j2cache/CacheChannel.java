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
	 * 读取缓存中的字节数组
	 * @param region Cache region name
	 * @param key cache key
	 * @return cache object
	 */
	public CacheObject<byte[]> getBytes(String region, String key)  {
		return _getObject(region, key, byte[].class);
	}

	/**
	 * 读取缓存中的字符串
	 * @param region Cache region name
	 * @param key cache key
	 * @return cache object
	 */
	public CacheObject<String> getString(String region, String key)  {
		return _getObject(region, key, String.class);
	}

	/**
	 * 读取缓存中的整数
	 * @param region Cache region name
	 * @param key cache key
	 * @return cache object
	 */
	public CacheObject<Integer> getInteger(String region, String key)  {
		return _getObject(region, key, int.class);
	}

	/**
	 * 读取缓存中的长整数
	 * @param region Cache region name
	 * @param key cache key
	 * @return cache object
	 */
	public CacheObject<Long> getLong(String region, String key)  {
		return _getObject(region, key, long.class);
	}

	/**
	 * 读取缓存中的双精度数
	 * @param region Cache region name
	 * @param key cache key
	 * @return cache object
	 */
	public CacheObject<Double> getDouble(String region, String key) { return _getObject(region, key, double.class); }

	/**
	 * 读取缓存中的对象
	 * @param region Cache region name
	 * @param key cache key
	 * @return cache object
	 */
	public CacheObject<Object> get(String region, String key)  {
		return _getObject(region, key, Object.class);
	}

	private <T> CacheObject<T> _getObject(String region, String key, Class<T> cls)  {
		CacheObject<T> obj = new CacheObject<>(region, key, Cache.LEVEL_1);
		obj.setValue((T)CacheProviderHolder.getLevel1Cache(region).get(key));
		if(obj.getValue() == null) {

			obj.setLevel(Cache.LEVEL_2);

			if(cls.equals(String.class))
				obj.setValue((T)CacheProviderHolder.getLevel2Cache(region).get(key));
			else if(cls.equals(int.class) || cls.equals(long.class) || cls.equals(Integer.class) || cls.equals(Long.class) || cls.equals(short.class) || cls.equals(byte.class) || cls.equals(Short.class) || cls.equals(Byte.class)) {
				Object cachedObj = CacheProviderHolder.getLevel2Cache(region).get(key);
				obj.setValue((T) ((cachedObj != null) ? Long.valueOf((String) cachedObj) : null));
			}
			else if(cls.equals(float.class) || cls.equals(double.class) || cls.equals(Float.class) || cls.equals(Double.class)) {
				Object cachedObj = CacheProviderHolder.getLevel2Cache(region).get(key);
				obj.setValue((T) ((cachedObj != null) ? Double.valueOf((String) cachedObj) : null));
			}
			else if(cls.equals(byte[].class))
				obj.setValue((T)CacheProviderHolder.getLevel2Cache(region).getBytes(key));
			else
				obj.setValue((T)CacheProviderHolder.getLevel2Cache(region).get(key));

			if(obj.getValue() != null){
				CacheProviderHolder.getLevel1Cache(region).put(key, obj.getValue());
			}
		}
		return obj;
	}

	/**
	 * 批量读取缓存中的整数
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache object
	 */
	public Map<String, CacheObject<Integer>> getInteger(String region, Collection<String> keys)  {
		return _getObjects(region, keys, int.class);
	}

	/**
	 * 批量读取缓存中的长整数
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache object
	 */
	public Map<String, CacheObject<Long>> getLong(String region, Collection<String> keys)  {
		return _getObjects(region, keys, long.class);
	}

	/**
	 * 批量读取缓存中的字符串
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache object
	 */
	public Map<String, CacheObject<String>> getString(String region, Collection<String> keys)  {
		return _getObjects(region, keys, String.class);
	}

	/**
	 * 批量读取缓存中的字节数组
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache object
	 */
	public Map<String, CacheObject<byte[]>> getBytes(String region, Collection<String> keys)  {
		return _getObjects(region, keys, byte[].class);
	}

	/**
	 * 批量读取缓存中的对象
	 * @param region Cache region name
	 * @param keys cache keys
	 * @return cache object
	 */
	public Map<String, CacheObject<Object>> get(String region, Collection<String> keys)  {
		return _getObjects(region, keys, Object.class);
	}

	/**
	 * 批量读取缓存中的对象
	 * @param region Cache region name
	 * @param keys cache keys
	 * @param cls cache object class
	 * @return cache object
	 */
	private <T> Map<String, CacheObject<T>> _getObjects(String region, Collection<String> keys, Class<T> cls)  {
		Map<String, CacheObject<T>> values = new HashMap<>();
		List<String> keys_not_in_level_1 = new ArrayList<>();
		for(String key : keys){
			T obj = (T)CacheProviderHolder.getLevel1Cache(region).get(key);
			if(obj != null) {
				CacheObject<T> cacheObject = new CacheObject<T>(region, key, Cache.LEVEL_1, obj);
				values.put(key, cacheObject);
			}
			else
				keys_not_in_level_1.add(key);
		}

		for(String key : keys_not_in_level_1)
			values.put(key, _getObject(region, key, cls));

		return values;
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
	 * 缓存数值增加 l
	 * @param region Cache region name
	 * @param key cache key
	 * @param l increment value
	 * @return new value
	 */
	public long incr(String region, String key, long l)  {
		long newValue = CacheProviderHolder.getLevel2Cache(region).incr(key, l);
		CacheProviderHolder.getLevel1Cache(region).put(key, newValue);
		this.sendEvictCmd(region, key);//清除原有的一级缓存的内容，使之重新加载
		return newValue;
	}

	/**
	 * 缓存数值减去 l
	 * @param region Cache region name
	 * @param key cache key
	 * @param l decrement value
	 * @return new value
	 */
	public long decr(String region, String key, long l)  {
		long newValue = CacheProviderHolder.getLevel2Cache(region).decr(key, l);
		CacheProviderHolder.getLevel1Cache(region).put(key, newValue);
		this.sendEvictCmd(region, key);//清除原有的一级缓存的内容，使之重新加载
		return newValue;
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
			CacheProviderHolder.getLevel1Cache(region).put(key, value);
			CacheProviderHolder.getLevel2Cache(region).put(key, value);
			this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
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
				CacheProviderHolder.getLevel1Cache(region, timeToLiveInSeconds).put(key, value);
				CacheProviderHolder.getLevel2Cache(region).put(key, value);
				this.sendEvictCmd(region, key);//清除原有的一级缓存的内容
			}
		}
	}

	/**
	 * 批量插入数据
	 * @param region Cache Region name
	 * @param elements Cache Elements
	 */
	public void set(String region, Map<String, Object> elements)  {
		CacheProviderHolder.getLevel1Cache(region).put(elements);
		CacheProviderHolder.getLevel2Cache(region).put(elements);
		//广播
		this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
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
			CacheProviderHolder.getLevel1Cache(region, timeToLiveInSeconds).put(elements);
			CacheProviderHolder.getLevel2Cache(region).put(elements);
			//广播
			this.sendEvictCmd(region, elements.keySet().stream().toArray(String[]::new));
		}
	}

	/**
	 * Remove cached data in J2Cache
	 *
	 * @param region:  Cache Region name
	 * @param keys: Cache key
	 */
	public void evict(String region, String...keys)  {
        CacheProviderHolder.getLevel1Cache(region).evict(keys);
        CacheProviderHolder.getLevel2Cache(region).evict(keys);
        this.sendEvictCmd(region, keys); //发送广播
    }

	/**
	 * Clear the cache
	 *
	 * @param region: Cache region name
	 */
	public void clear(String region)  {
        CacheProviderHolder.getLevel1Cache(region).clear();
		CacheProviderHolder.getLevel2Cache(region).clear();
		this.sendClearCmd(region);
    }
	
	/**
	 * <p>Get cache region keys</p>
	 * <p><strong>Notice: ehcache3 not support keys</strong></p>
	 *
	 * @param region: Cache region name
	 * @return key list
	 */
	public Collection<String> keys(String region)  {
        return CacheProviderHolder.getLevel1Cache(region).keys();
    }

	/**
	 * Close J2Cache
	 */
	public abstract void close();

}
