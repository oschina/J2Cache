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
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject<byte[]> getBytes(String region, String key) throws IOException {
		return _getObject(region, key, byte[].class);
	}

	/**
	 * 读取缓存中的字符串
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject<String> getString(String region, String key) throws IOException {
		return _getObject(region, key, String.class);
	}

	/**
	 * 读取缓存中的整数
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject<Integer> getInteger(String region, String key) throws IOException {
		return _getObject(region, key, int.class);
	}

	/**
	 * 读取缓存中的长整数
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject<Long> getLong(String region, String key) throws IOException {
		return _getObject(region, key, long.class);
	}

	/**
	 * 读取缓存中的对象
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject<Serializable> getObject(String region, String key) throws IOException {
		return _getObject(region, key, Serializable.class);
	}

	private <T> CacheObject<T> _getObject(String region, String key, Class<T> cls) throws IOException {
		CacheObject<T> obj = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1);
		obj.setValue((T)CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key));
		if(obj.getValue() == null) {
			if(cls.equals(String.class))
				obj.setValue((T)CacheProviderHolder.getString(region, key));
			else if(cls.equals(int.class) || cls.equals(long.class) || cls.equals(Integer.class) || cls.equals(Long.class))
				obj.setValue((T)CacheProviderHolder.getLong(region, key));
			else if(cls.equals(byte[].class))
				obj.setValue((T)CacheProviderHolder.getBytes(region, key));
			else
				obj.setValue((T)CacheProviderHolder.getObject(region, key));

			if(obj.getValue() != null){
				obj.setLevel(CacheProviderHolder.LEVEL_2);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, (Serializable)obj.getValue());
			}
		}
		return obj;
	}

	/**
	 * 批量读取缓存中的整数
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<Integer>> getInteger(String region, Collection<String> keys) throws IOException {
		return _getObjects(region, keys, int.class);
	}

	/**
	 * 批量读取缓存中的长整数
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<Long>> getLong(String region, Collection<String> keys) throws IOException {
		return _getObjects(region, keys, long.class);
	}

	/**
	 * 批量读取缓存中的字符串
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<String>> getString(String region, Collection<String> keys) throws IOException {
		return _getObjects(region, keys, String.class);
	}

	/**
	 * 批量读取缓存中的字节数组
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<byte[]>> getBytes(String region, Collection<String> keys) throws IOException {
		return _getObjects(region, keys, byte[].class);
	}

	/**
	 * 批量读取缓存中的对象
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<Serializable>> getObjects(String region, Collection<String> keys) throws IOException {
		return _getObjects(region, keys, Serializable.class);
	}

	/**
	 * 批量读取缓存中的对象
	 * @param region
	 * @param keys
	 * @param cls
	 * @return
	 * @throws IOException
	 */
	private <T> Map<String, CacheObject<T>> _getObjects(String region, Collection<String> keys, Class<T> cls) throws IOException {
		Map<String, CacheObject<T>> values = new HashMap<>();
		List<String> keys_not_in_level_1 = new ArrayList<>();
		for(String key : keys){
			T obj = (T)CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key);
			if(obj != null) {
				CacheObject<T> cacheObject = new CacheObject<T>(region, key, CacheProviderHolder.LEVEL_1, obj);
				values.put(key, cacheObject);
			}
			else
				keys_not_in_level_1.add(key);
		}
		for(String key : keys_not_in_level_1) {
			Serializable obj;
			if(cls.equals(String.class))
				obj = CacheProviderHolder.getString(region ,key);
			else if(cls.equals(byte[].class))
				obj = CacheProviderHolder.getBytes(region ,key);
			else if(cls.equals(int.class) || cls.equals(long.class) || cls.equals(Integer.class) || cls.equals(Long.class))
				obj = CacheProviderHolder.getLong(region, key);
			else
				obj = CacheProviderHolder.getObject(region, key);
			if(obj != null) {
				CacheObject<T> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_2, (T)obj);
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
	 * 缓存数值增加 l
	 * @param region Cache region name
	 * @param key cache key
	 * @param l increment value
	 * @return new value
	 * @throws IOException io exception
	 */
	public long incr(String region, String key, long l) throws IOException {
		long newValue = CacheProviderHolder.incr(region, key, l);
		CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, newValue);
		this.sendEvictCmd(region, key);//清除原有的一级缓存的内容，使之重新加载
		return newValue;
	}

	/**
	 * 缓存数值减去 l
	 * @param region Cache region name
	 * @param key cache key
	 * @param l decrement value
	 * @return new value
	 * @throws IOException io exception
	 */
	public long decr(String region, String key, long l) throws IOException {
		long newValue = CacheProviderHolder.decr(region, key, l);
		CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, newValue);
		this.sendEvictCmd(region, key);//清除原有的一级缓存的内容，使之重新加载
		return newValue;
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
