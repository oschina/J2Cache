package net.oschina.j2cache;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import net.oschina.j2cache.ehcache.EhCacheProvider;
import net.oschina.j2cache.redis.RedisCacheProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存管理器
 * @author liudong
 */
class CacheManager {

	private final static Logger log = LoggerFactory.getLogger(CacheManager.class);
	private final static String CONFIG_FILE = "/j2cache.properties";

	private static CacheProvider l1_provider;
	private static CacheProvider l2_provider;
	
	private static CacheExpiredListener listener;
	
	public static void initCacheProvider(CacheExpiredListener listener){

		InputStream configStream = CacheManager.class.getClassLoader().getParent().getResourceAsStream(CONFIG_FILE);
		if(configStream == null)
			configStream = CacheManager.class.getResourceAsStream(CONFIG_FILE);
		if(configStream == null)
			throw new CacheException("Cannot find " + CONFIG_FILE + " !!!");
		
		Properties props = new Properties();
		
		CacheManager.listener = listener;
		try{
			props.load(configStream);
			configStream.close();
			
			CacheManager.l1_provider = getProviderInstance(props.getProperty("cache.L1.provider_class"));
			CacheManager.l1_provider.start(getProviderProperties(props, CacheManager.l1_provider));
			log.info("Using L1 CacheProvider : " + l1_provider.getClass().getName());
			
			CacheManager.l2_provider = getProviderInstance(props.getProperty("cache.L2.provider_class"));
			CacheManager.l2_provider.start(getProviderProperties(props, CacheManager.l2_provider));
			log.info("Using L2 CacheProvider : " + l2_provider.getClass().getName());
			
		}catch(Exception e){
			throw new CacheException("Unabled to initialize cache providers", e);
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

	/**
	 * 获取缓存中的数据
	 * @param level
	 * @param name
	 * @param key
	 * @return
	 */
	public final static Object get(int level, String name, Serializable key){
		//System.out.println("GET1 => " + name+":"+key);
		if(name!=null && key != null) {
            Cache cache = _GetCache(level, name, true);
            if (cache != null)
                return cache.get(key);
        }
		return null;
	}
	
	/**
	 * 获取缓存中的数据
	 * @param <T>
	 * @param level
	 * @param resultClass
	 * @param name
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static <T> T get(int level, Class<T> resultClass, String name, Serializable key){
		//System.out.println("GET2 => " + name+":"+key);
		if(name!=null && key != null) {
            Cache cache =_GetCache(level, name, true);
            if (cache != null)
                return (T)cache.get(key);
        }
		return null;
	}
	
	/**
	 * 写入缓存
	 * @param level
	 * @param name
	 * @param key
	 * @param value
	 */
	public final static void set(int level, String name, Serializable key, Serializable value){
		//System.out.println("SET => " + name+":"+key+"="+value);
		if(name!=null && key != null && value!=null) {
            Cache cache =_GetCache(level, name, true);
            if (cache != null)
                cache.put(key,value);
        }
	}
	
	/**
	 * 清除缓存中的某个数据
	 * @param level
	 * @param name
	 * @param key
	 */
	public final static void evict(int level, String name, Serializable key){
		//batchEvict(level, name, Arrays.asList((Object)key));
		
		if(name!=null && key != null) {
            Cache cache =_GetCache(level, name, true);
            if (cache != null)
                cache.remove(key);
        }
        
	}
	
	/**
	 * 批量删除缓存中的一些数据
	 * @param level
	 * @param name
	 * @param keys
	 */
	public final static void batchEvict(int level, String name, List<Object> keys) {
		if(name!=null && keys != null && keys.size() > 0) {
            Cache cache =_GetCache(level, name, true);
            if (cache != null)
                cache.batchRemove(keys);
        }
	}

	/**
	 * 清除缓存中的某个数据
	 * @param level
	 * @param name
	 * @param key
	 */
	public final static void justEvict(int level, String name, Serializable key){
		if(name!=null && key != null){
			Cache cache = _GetCache(level, name, false);
			if(cache != null)
				cache.remove(key);
		}
	}

}
