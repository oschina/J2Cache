package net.oschina.j2cache.redis;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.CacheProvider;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

/**
 * Redis 缓存实现
 * @author Winter Lau
 */
public class RedisCacheProvider implements CacheProvider {

	private static String host;
	private static int port;
	private static int timeout;
	private static String password;
	private static int database;
	
	private static JedisPool pool;
	
	@Override
	public String name() {
		return "redis";
	}

	/**
	 * 释放资源
	 * @param jedis  jedis instance
	 * @param isBrokenResource resource is ok or not
	 */
    public static void returnResource(Jedis jedis,boolean isBrokenResource) {
    	if(null == jedis)
    		return;
    	pool.returnResourceObject(jedis);
    	
        if(isBrokenResource){
        	jedis.close();
            jedis = null;
        }
        else
        	pool.returnResourceObject(jedis);
        
    }
    
    public static Jedis getResource() {
    	return pool.getResource();
    }

	@Override
	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) throws CacheException {
		return new RedisCache(regionName);
    }

	@Override
	public void start(Properties props) throws CacheException {
		JedisPoolConfig config = new JedisPoolConfig();
		
		host = getProperty(props, "host", "127.0.0.1");
		password = props.getProperty("password", null);
		
		port = getProperty(props, "port", 6379);
		timeout = getProperty(props, "timeout", 2000);
		database = getProperty(props, "database", 0);

		config.setBlockWhenExhausted(getProperty(props, "blockWhenExhausted", true));
		config.setMaxIdle(getProperty(props, "maxIdle", 10));
		config.setMinIdle(getProperty(props, "minIdle", 5));
//		config.setMaxActive(getProperty(props, "maxActive", 50));
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
