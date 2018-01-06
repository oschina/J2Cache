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

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import net.oschina.j2cache.caffeine.CaffeineProvider;
import net.oschina.j2cache.ehcache.EhCacheProvider3;
import net.oschina.j2cache.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oschina.j2cache.ehcache.EhCacheProvider;
import net.oschina.j2cache.redis.RedisCacheProvider;

/**
 * 两级的缓存管理器
 * @author Winter Lau(javayou@gmail.com)
 */
class CacheProviderHolder {

	private final static Logger log = LoggerFactory.getLogger(CacheProviderHolder.class);

	public final static byte LEVEL_1 = 1;
	public final static byte LEVEL_2 = 2;

	private static CacheProvider l1_provider;
	private static CacheProvider l2_provider;

	private static CacheExpiredListener listener;

	
	/**
	 * Initialize Cache Provider
	 * @param listener cache listener
	 */
	public static void initCacheProvider(Properties props, CacheExpiredListener listener){
		CacheProviderHolder.listener = listener;
		try{
			CacheProviderHolder.l1_provider = getProviderInstance(props.getProperty("j2cache.L1.provider_class"));
			CacheProviderHolder.l1_provider.start(getProviderProperties(props, CacheProviderHolder.l1_provider));
			log.info("Using L1 CacheProvider : " + l1_provider.getClass().getName());
			
			CacheProviderHolder.l2_provider = getProviderInstance(props.getProperty("j2cache.L2.provider_class"));
			CacheProviderHolder.l2_provider.start(getProviderProperties(props, CacheProviderHolder.l2_provider));
			log.info("Using L2 CacheProvider : " + l2_provider.getClass().getName());

		}catch(Exception e){
			throw new CacheException("Failed to initialize cache manager", e);
		}
	}

	/**
	 * FIXME 此代码让整个接口设计变得很糟糕
	 * @return
	 */
	public static RedisClient getRedisClient() {
		return ((RedisCacheProvider)l2_provider).getClient();
	}

	private final static CacheProvider getProviderInstance(String cacheIdent) throws Exception {
		if("ehcache".equalsIgnoreCase(cacheIdent))
			return new EhCacheProvider();
		if("ehcache3".equalsIgnoreCase(cacheIdent))
			return new EhCacheProvider3();
		if("caffeine".equalsIgnoreCase(cacheIdent))
			return new CaffeineProvider();
		if("redis".equalsIgnoreCase(cacheIdent))
			return new RedisCacheProvider();
		if("none".equalsIgnoreCase(cacheIdent))
			return new NullCacheProvider();
		return (CacheProvider)Class.forName(cacheIdent).newInstance();
	}
	
	private final static Properties getProviderProperties(Properties props, CacheProvider provider) {
		Properties new_props = new Properties();
		Enumeration<Object> keys = props.keys();
		String prefix = provider.name() + '.';
		while(keys.hasMoreElements()){
			String key = (String)keys.nextElement();
			if(key.startsWith(prefix))
				new_props.setProperty(key.substring(prefix.length()), props.getProperty(key));
		}
		return new_props;
	}
	
	private final static Cache getCache(int level, String cache_name, boolean autoCreate) {
		return ((level==1)?l1_provider:l2_provider).buildCache(cache_name, listener);
	}

	private final static Cache getCache(int level, String cache_name, long timeToLiveInSeconds) {
		if(timeToLiveInSeconds <= 0)
			return getCache(level, cache_name, true);
		return ((level==1)?l1_provider:l2_provider).buildCache(cache_name, timeToLiveInSeconds, listener);
	}
	
	public final static void shutdown() {
		l1_provider.stop();
		l2_provider.stop();
	}

	/**
	 * 获取缓存中的数据
	 * @param level Cache Level: L1 and L2
	 * @param name Cache region name
	 * @param key Cache key
	 * @return Cache object
	 */
	public final static Serializable get(int level, String name, String key) throws IOException {
		//System.out.println("GET1 => " + name+":"+key);
		if(name!=null && key != null) {
            Cache cache = getCache(level, name, false);
            if (cache != null)
                return cache.get(key);
        }
		return null;
	}

	/**
	 * 批量获取缓存对象
	 * @param level
	 * @param region
	 * @param keys
	 * @return
	 */
	public final static Map<String, Serializable> getAll(int level, String region, Set<String> keys) throws IOException {
		Cache cache = getCache(level, region, false);
		return (cache!=null)?cache.getAll(keys):null;
	}

	/**
	 * 判断某个缓存键是否存在
	 * @param level
	 * @param region
	 * @param key
	 * @return
	 */
	public final static boolean exists(int level, String region, String key) throws IOException {
		Cache cache = getCache(level, region, false);
		return (cache!=null)?cache.exists(key):false;
	}

	/**
	 * 写入缓存
	 * @param level Cache Level: L1 and L2
	 * @param region Cache region name
	 * @param key Cache key
	 * @param value Cache value
	 */
	public final static void set(int level, String region, String key, Serializable value) throws IOException {
		Cache cache = getCache(level, region, true);
		cache.put(key, value);
	}

	public final static void set(int level, String region, String key, Serializable value, long timeToLiveInSeconds) throws IOException {
		Cache cache = getCache(level, region, timeToLiveInSeconds);
		cache.put(key, value);
	}

	/**
	 * Put an element in the cache if no element is currently mapped to the elements key.
	 * @param level
	 * @param region
	 * @param key
	 * @param value
	 * @return
	 * @throws IOException
	 */
	public final static Serializable setIfAbsent(int level, String region, String key, Serializable value) throws IOException {
		Cache cache = getCache(level, region, true);
		return cache.putIfAbsent(key, value);
	}


	public final static Serializable setIfAbsent(int level, String region, String key, Serializable value, long timeToLiveInSeconds) throws IOException {
		Cache cache = getCache(level, region, timeToLiveInSeconds);
		return cache.putIfAbsent(key, value);
	}

	/**
	 * 批量插入数据
	 * @param level
	 * @param region
	 * @param elements
	 */
	public final static void setAll(int level, String region, Map<String, Serializable> elements) throws IOException {
		Cache cache = getCache(level, region, true);
		cache.putAll(elements);
	}

	public final static void setAll(int level, String region, Map<String, Serializable> elements, long timeToLiveInSeconds) throws IOException {
		Cache cache = getCache(level, region, timeToLiveInSeconds);
		cache.putAll(elements);
	}

	/**
	 * 清除缓存中的某个数据
	 * @param level Cache Level: L1 and L2
	 * @param name Cache region name
	 * @param keys Cache key
	 */
	public final static void evict(int level, String name, String...keys) throws IOException {
		if(name!=null && keys != null && keys.length > 0) {
			Cache cache = getCache(level, name, false);
			if (cache != null)
				cache.evict(keys);
		}
	}

	/**
	 * Clear the cache
	 * @param level Cache level
	 * @param name cache region name
	 */
	public final static void clear(int level, String name) throws IOException {
        Cache cache = getCache(level, name, false);
        if(cache != null)
        	cache.clear();
	}
	
	/**
	 * list cache keys
	 * @param level Cache level
	 * @param name cache region name
	 * @return Key List
	 */
	public final static Collection<String> keys(int level, String name) throws IOException {
        Cache cache = getCache(level, name, false);
		return (cache!=null)?cache.keys():null;
	}
	
}
