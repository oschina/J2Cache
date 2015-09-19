package net.oschina.j2cache;

import java.util.Properties;

/**
 * @author winterlau
 */
public class NullCacheProvider implements CacheProvider {

	private final static NullCache cache = new NullCache();

	@Override
	public String name() {
		return "none";
	}

	/* (non-Javadoc)
	 * @see net.oschina.j2cache.CacheProvider#buildCache(java.lang.String, boolean, net.oschina.j2cache.CacheExpiredListener)
	 */
	@Override
	public Cache buildCache(String regionName, boolean autoCreate,
			CacheExpiredListener listener) throws CacheException {
		return cache;
	}

	/* (non-Javadoc)
	 * @see net.oschina.j2cache.CacheProvider#start()
	 */
	@Override
	public void start(Properties props) throws CacheException {
	}

	/* (non-Javadoc)
	 * @see net.oschina.j2cache.CacheProvider#stop()
	 */
	@Override
	public void stop() {
	}

}
