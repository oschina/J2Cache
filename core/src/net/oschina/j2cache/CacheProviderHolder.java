package net.oschina.j2cache;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oschina.j2cache.ehcache.EhCacheProvider;
import net.oschina.j2cache.redis.RedisCacheProvider;

/**
 * 缓存管理器
 * @author Winter Lau
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

	private final static CacheProvider getProviderInstance(String value) throws Exception {
		if("ehcache".equalsIgnoreCase(value))
			return new EhCacheProvider();
		if("redis".equalsIgnoreCase(value))
			return new RedisCacheProvider();
		if("none".equalsIgnoreCase(value))
			return new NullCacheProvider();
		return (CacheProvider)Class.forName(value).newInstance();
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
	public final static Object get(int level, String name, Object key) throws IOException {
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
	public final static void set(int level, String name, Object key, Object value) throws IOException {
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
	public final static void evict(int level, String name, Object key) throws IOException {
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
	@SuppressWarnings("rawtypes")
	public final static void evicts(int level, String name, List keys) throws IOException {
		if(name!=null && keys != null && keys.size() > 0) {
            Cache cache =_GetCache(level, name, false);
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
	@SuppressWarnings("rawtypes")
	public final static Set keys(int level, String name) throws IOException {
        Cache cache =_GetCache(level, name, false);
		return (cache!=null)?cache.keys():null;
	}
	
}
