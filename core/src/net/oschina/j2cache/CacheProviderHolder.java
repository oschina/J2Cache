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
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import net.oschina.j2cache.ehcache.EhCacheProvider3;
import net.oschina.j2cache.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oschina.j2cache.ehcache.EhCacheProvider;
import net.oschina.j2cache.redis.RedisCacheProvider;
import redis.clients.jedis.JedisCluster;

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
	
	private final static Cache _GetCache(int level, String cache_name, boolean autoCreate) {
		return ((level==1)?l1_provider:l2_provider).buildCache(cache_name, autoCreate, listener);
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
	public final static Serializable get(int level, String name, Serializable key) throws IOException {
		//System.out.println("GET1 => " + name+":"+key);
		if(name!=null && key != null) {
            Cache cache = _GetCache(level, name, false);
            if (cache != null)
                return cache.get(key);
        }
		return null;
	}
	
	/**
	 * 写入缓存
	 * @param level Cache Level: L1 and L2
	 * @param name Cache region name
	 * @param key Cache key
	 * @param value Cache value
	 */
	public final static void set(int level, String name, Serializable key, Serializable value) throws IOException {
		//System.out.println("SET => " + name+":"+key+"="+value);
		if(name!=null && key != null && value!=null) {
            Cache cache =_GetCache(level, name, true);
            if (cache != null)
                cache.put(key,value);
        }
	}

	/**
	 * 清除缓存中的某个数据
	 * @param level Cache Level: L1 and L2
	 * @param name Cache region name
	 * @param key Cache key
	 */
	public final static void evict(int level, String name, Serializable key) throws IOException {
		//batchEvict(level, name, java.util.Arrays.asList(key));
		if(name!=null && key != null) {
            Cache cache =_GetCache(level, name, false);
            if (cache != null)
                cache.evict(key);
        }
	}
	
	/**
	 * 批量删除缓存中的一些数据
	 * @param level Cache Level： L1 and L2
	 * @param name Cache region name
	 * @param keys Cache keys
	 */
	public final static void evicts(int level, String name, List<Serializable> keys) throws IOException {
		if(name!=null && keys != null && keys.size() > 0) {
            Cache cache =_GetCache(level, name, false);
            if (cache != null)
                cache.evicts(keys);
        }
	}

	/**
	 * Clear the cache
	 * @param level Cache level
	 * @param name cache region name
	 */
	public final static void clear(int level, String name) throws IOException {
        Cache cache =_GetCache(level, name, false);
        if(cache != null)
        	cache.clear();
	}
	
	/**
	 * list cache keys
	 * @param level Cache level
	 * @param name cache region name
	 * @return Key List
	 */
	public final static Set<Serializable> keys(int level, String name) throws IOException {
        Cache cache =_GetCache(level, name, false);
		return (cache!=null)?cache.keys():null;
	}
	
}
