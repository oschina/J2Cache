package net.oschina.j2cache.redis;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheExpiredListener;

public class Redis2CacheProvider extends RedisCacheProvider {
	
	public String name() {
		return "redis2";
	}

	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) throws CacheException {
		return new RedisCache2(regionName);
	}
}
