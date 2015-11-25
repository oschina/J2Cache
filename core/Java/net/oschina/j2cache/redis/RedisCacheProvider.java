package net.oschina.j2cache.redis;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.CacheProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis 缓存实现
 * @author Winter Lau
 * @author wendal
 */
public class RedisCacheProvider implements CacheProvider {
	
	private static JedisPool pool;
	
	protected ConcurrentHashMap<String, RedisCache> caches = new ConcurrentHashMap<>();
	
	public String name() {
		return "redis";
	}
    
	// 这个实现有个问题,如果不使用RedisCacheProvider,但又使用RedisCacheChannel,这就NPE了
    public static Jedis getResource() {
    	return pool.getResource();
    }

	@Override
	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) throws CacheException {
		// 虽然这个实现在并发时有概率出现同一各regionName返回不同的实例
		// 但返回的实例一次性使用,所以加锁了并没有增加收益
		RedisCache cache = caches.get(regionName);
		if (cache == null) {
			cache = new RedisCache(regionName, pool);
			caches.put(regionName, cache);
		}
		return cache;
    }

	@Override
	public void start(Properties props) throws CacheException {
		JedisPoolConfig config = new JedisPoolConfig();
		
		String host = getProperty(props, "host", "127.0.0.1");
		String password = props.getProperty("password", null);
		
		int port = getProperty(props, "port", 6379);
		int timeout = getProperty(props, "timeout", 2000);
		int database = getProperty(props, "database", 0);

		config.setBlockWhenExhausted(getProperty(props, "blockWhenExhausted", true));
		config.setMaxIdle(getProperty(props, "maxIdle", 10));
		config.setMinIdle(getProperty(props, "minIdle", 5));
//		config.setMaxActive(getProperty(props, "maxActive", 50));
		config.setMaxTotal(getProperty(props, "maxTotal", 10000));
		config.setMaxWaitMillis(getProperty(props, "maxWait", 100));
		config.setTestWhileIdle(getProperty(props, "testWhileIdle", false));
		config.setTestOnBorrow(getProperty(props, "testOnBorrow", true));
		config.setTestOnReturn(getProperty(props, "testOnReturn", false));
		config.setNumTestsPerEvictionRun(getProperty(props, "numTestsPerEvictionRun", 10));
		config.setMinEvictableIdleTimeMillis(getProperty(props, "minEvictableIdleTimeMillis", 1000));
		config.setSoftMinEvictableIdleTimeMillis(getProperty(props, "softMinEvictableIdleTimeMillis", 10));
		config.setTimeBetweenEvictionRunsMillis(getProperty(props, "timeBetweenEvictionRunsMillis", 10));
		config.setLifo(getProperty(props, "lifo", false));

		pool = new JedisPool(config, host, port, timeout, password, database);
		
	}

	@Override
	public void stop() {
		pool.destroy();
		caches.clear();
	}

	private static String getProperty(Properties props, String key, String defaultValue) {
		return props.getProperty(key, defaultValue).trim();
	}

	private static int getProperty(Properties props, String key, int defaultValue) {
		try{
			return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)).trim());
		}catch(Exception e){
			return defaultValue;
		}
	}

	private static boolean getProperty(Properties props, String key, boolean defaultValue) {
		return "true".equalsIgnoreCase(props.getProperty(key, String.valueOf(defaultValue)).trim());
	}
}
