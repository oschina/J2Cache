package net.oschina.j2cache.ehcache;

import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.CacheProvider;
import net.sf.ehcache.CacheManager;

/**
 * EhCache 2.x 缓存管理器的封装，用来管理多个缓存区域
 *
 * @author Winter Lau (javayou@gmail.com)
 * @author wendal
 */
public class EhCacheProvider implements CacheProvider {

	private final static Logger log = LoggerFactory.getLogger(EhCacheProvider.class);
	public static String KEY_EHCACHE_NAME = "name";
	public static String KEY_EHCACHE_CONFIG_XML = "configXml";

	private CacheManager manager;
	private ConcurrentHashMap<String, EhCache> _CacheManager ;

	@Override
	public String name() {
		return "ehcache";
	}

    /**
     * Builds a Cache.
     * Even though this method provides properties, they are not used.
     * Properties for EHCache are specified in the ehcache.xml file.
     * Configuration will be read from ehcache.xml for a cache declaration
     * where the regionName attribute matches the regionName parameter in this builder.
     *
     * @param regionName the regionName of the cache. Must match a cache configured in ehcache.xml
     * @param autoCreate auto create cache region ?
     * @param listener cache listener
     * @return a newly built cache will be built and initialised
     */
    @Override
    public EhCache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
    	EhCache ehcache = _CacheManager.get(regionName);
    	if(ehcache == null && autoCreate){
			synchronized(EhCacheProvider.class){
				ehcache = _CacheManager.get(regionName);
				if(ehcache == null){
					net.sf.ehcache.Cache cache = manager.getCache(regionName);
					if (cache == null) {
						log.warn("Could not find configuration [" + regionName + "]; using defaults.");
						manager.addCache(regionName);
						cache = manager.getCache(regionName);
						log.debug("started Ehcache region: " + regionName);
					}
					ehcache = new EhCache(cache, listener);
					_CacheManager.put(regionName, ehcache);
				}
			}
    	}
        return ehcache;
    }

	/**
	 * init ehcache config
	 *
	 * @param props current configuration settings.
	 */
	public void start(Properties props) {
		if (manager != null) {
            log.warn("Attempt to restart an already started EhCacheProvider.");
            return;
        }
		
		// 如果指定了名称,那么尝试获取已有实例
		String ehcacheName = (String)props.get(KEY_EHCACHE_NAME);
		if (ehcacheName != null && ehcacheName.trim().length() > 0)
			manager = CacheManager.getCacheManager(ehcacheName);
		if (manager == null) {
			// 指定了配置文件路径? 加载之
			if (props.containsKey(KEY_EHCACHE_CONFIG_XML)) {
				URL url = getClass().getResource(props.getProperty(KEY_EHCACHE_CONFIG_XML));
				manager = CacheManager.newInstance(url);//props.getProperty(KEY_EHCACHE_CONFIG_XML));
			} else {
				// 加载默认实例
				manager = CacheManager.getInstance();
			}
		}
        _CacheManager = new ConcurrentHashMap<>();
	}

	/**
	 * Callback to perform any necessary cleanup of the underlying cache implementation.
	 */
	public void stop() {
		if (manager != null) {
            manager.shutdown();
            _CacheManager.clear();
            manager = null;
        }
	}

}
