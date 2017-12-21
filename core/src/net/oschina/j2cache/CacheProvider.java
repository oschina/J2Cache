package net.oschina.j2cache;

import java.util.Properties;

/**
 * Support for pluggable caches.
 * @author winterlau
 */
public interface CacheProvider {

	/**
	 * 缓存的标识名称
	 * @return return cache provider name
	 */
	String name();
	
	/**
	 * Configure the cache
	 *
	 * @param regionName the name of the cache region
	 * @param autoCreate autoCreate settings
	 * @param listener listener for expired elements
	 * @return return cache instance
	 */
	Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener);

	/**
	 * Callback to perform any necessary initialization of the underlying cache implementation
	 * during SessionFactory construction.
	 *
	 * @param props current configuration settings.
	 */
	void start(Properties props);

	/**
	 * Callback to perform any necessary cleanup of the underlying cache implementation
	 * during SessionFactory.close().
	 */
	void stop();
	
}
