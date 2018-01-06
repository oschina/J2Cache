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
	public CacheObject getBytes(String region, String key) throws IOException {
		CacheObject<byte[]> obj = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1);
		obj.setValue((byte[])CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key));
		if(obj.getValue() == null) {
			obj.setValue(CacheProviderHolder.getBytes(region, key));
			if(obj.getValue() != null){
				obj.setLevel(CacheProviderHolder.LEVEL_2);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj.getValue());
			}
		}
		return obj;
	}

	/**
	 * 读取缓存中的字符串
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject getString(String region, String key) throws IOException {
		CacheObject<String> obj = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1);
		obj.setValue((String)CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key));
		if(obj.getValue() == null) {
			obj.setValue(CacheProviderHolder.getString(region, key));
			if(obj.getValue() != null){
				obj.setLevel(CacheProviderHolder.LEVEL_2);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj.getValue());
			}
		}
		return obj;
	}

	/**
	 * 读取缓存中的整数
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject getInteger(String region, String key) throws IOException {
		CacheObject longObj = getLong(region, key);
		CacheObject<Integer> obj = new CacheObject(region, key, longObj.getLevel());
		obj.setValue((longObj == null) ? null : ((Number)longObj.getValue()).intValue());
		return obj;
	}

	/**
	 * 读取缓存中的长整数
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject getLong(String region, String key) throws IOException {
		CacheObject<Long> obj = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1);
		obj.setValue((Long)CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key));
		if(obj.getValue() == null) {
			obj.setValue(CacheProviderHolder.getLong(region, key));
			if(obj.getValue() != null){
				obj.setLevel(CacheProviderHolder.LEVEL_2);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj.getValue());
			}
		}
		return obj;
	}

	/**
	 * 读取缓存中的对象
	 * @param region
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public CacheObject getObject(String region, String key) throws IOException {
		CacheObject<Serializable> obj = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1);
		obj.setValue(CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key));
		if(obj.getValue() == null) {
			obj.setValue(CacheProviderHolder.getObject(region, key));
			if(obj.getValue() != null){
				obj.setLevel(CacheProviderHolder.LEVEL_2);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj.getValue());
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
		Map<String, CacheObject<Long>> longs = getLong(region, keys);
		Map<String, CacheObject<Integer>> vals = new HashMap<>();
		longs.forEach((key, val) -> {
			CacheObject<Integer> obj = new CacheObject(region, key, val.getLevel());
			obj.setValue((val == null) ? null : ((Number)val.getValue()).intValue());
			vals.put(key, obj);
		});
		return vals;
	}

	/**
	 * 批量读取缓存中的长整数
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<Long>> getLong(String region, Collection<String> keys) throws IOException {
		Map<String, CacheObject<Long>> values = new HashMap<>();
		List<String> keys_not_in_level_1 = new ArrayList<>();
		for(String key : keys){
			Long obj = (Long)CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key);
			if(obj != null) {
				CacheObject<Long> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1, obj);
				values.put(key, cacheObject);
			}
			else
				keys_not_in_level_1.add(key);
		}
		for(String key : keys_not_in_level_1) {
			Long obj = CacheProviderHolder.getLong(region, key);
			if(obj != null) {
				CacheObject<Long> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_2, obj);
				values.put(key, cacheObject);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj);
			}
		}
		return values;
	}

	/**
	 * 批量读取缓存中的字符串
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<String>> getString(String region, Collection<String> keys) throws IOException {
		Map<String, CacheObject<String>> values = new HashMap<>();
		List<String> keys_not_in_level_1 = new ArrayList<>();
		for(String key : keys){
			String obj = (String)CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key);
			if(obj != null) {
				CacheObject<String> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1, obj);
				values.put(key, cacheObject);
			}
			else
				keys_not_in_level_1.add(key);
		}
		for(String key : keys_not_in_level_1) {
			String obj = CacheProviderHolder.getString(region, key);
			if(obj != null) {
				CacheObject<String> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_2, obj);
				values.put(key, cacheObject);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj);
			}
		}
		return values;
	}

	/**
	 * 批量读取缓存中的字节数组
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<byte[]>> getBytes(String region, Collection<String> keys) throws IOException {
		Map<String, CacheObject<byte[]>> values = new HashMap<>();
		List<String> keys_not_in_level_1 = new ArrayList<>();
		for(String key : keys){
			byte[] obj = (byte[])CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key);
			if(obj != null) {
				CacheObject<byte[]> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1, obj);
				values.put(key, cacheObject);
			}
			else
				keys_not_in_level_1.add(key);
		}
		for(String key : keys_not_in_level_1) {
			byte[] obj = CacheProviderHolder.getBytes(region, key);
			if(obj != null) {
				CacheObject<byte[]> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_2, obj);
				values.put(key, cacheObject);
				CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj);
			}
		}
		return values;
	}

	/**
	 * 批量读取缓存中的对象
	 * @param region
	 * @param keys
	 * @return
	 * @throws IOException
	 */
	public Map<String, CacheObject<Serializable>> getObjects(String region, Collection<String> keys) throws IOException {
		Map<String, CacheObject<Serializable>> values = new HashMap<>();
		List<String> keys_not_in_level_1 = new ArrayList<>();
		for(String key : keys){
			Serializable obj = CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key);
			if(obj != null) {
				CacheObject<Serializable> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_1, obj);
				values.put(key, cacheObject);
			}
			else
				keys_not_in_level_1.add(key);
		}
		for(String key : keys_not_in_level_1) {
			Serializable obj = CacheProviderHolder.getObject(region, key);
			if(obj != null) {
				CacheObject<Serializable> cacheObject = new CacheObject<>(region, key, CacheProviderHolder.LEVEL_2, obj);
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
