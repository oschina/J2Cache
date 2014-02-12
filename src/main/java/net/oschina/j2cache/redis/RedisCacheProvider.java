package net.oschina.j2cache.redis;

import java.util.Properties;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.CacheProvider;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

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
	 * @param jedis
	 * @param isBrokenResource
	 */
    public static void returnResource(BinaryJedis jedis,boolean isBrokenResource) {
    	if(null == jedis)
    		return;
        if(isBrokenResource){     
        	pool.returnBrokenResource(jedis);
            jedis = null;
        }
        else
        	pool.returnResource(jedis);
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
		
		host = getProperty(props, "host","127.0.0.1");
		password = props.getProperty("password", null);
		
		port = getProperty(props, "port", 6379);
		timeout = getProperty(props, "timeout", 2000);
		database = getProperty(props, "database", 0);
		
		config.setWhenExhaustedAction((byte)getProperty(props, "whenExhaustedAction",1));
		config.setMaxIdle(getProperty(props, "maxIdle", 10));
		config.setMinIdle(getProperty(props, "minIdle", 5));
		config.setMaxActive(getProperty(props, "maxActive", 50));
		config.setMaxWait(getProperty(props, "maxWait", 100));
		config.setTestWhileIdle(getProperty(props, "testWhileIdle", false));
		config.setTestOnBorrow(getProperty(props, "testOnBorrow", true));
		config.setTestOnReturn(getProperty(props, "testOnReturn", false));
		config.setNumTestsPerEvictionRun(getProperty(props, "numTestsPerEvictionRun", 10));
		config.setMinEvictableIdleTimeMillis(getProperty(props, "minEvictableIdleTimeMillis", 1000));
		config.setSoftMinEvictableIdleTimeMillis(getProperty(props, "softMinEvictableIdleTimeMillis", 10));
		config.setTimeBetweenEvictionRunsMillis(getProperty(props, "timeBetweenEvictionRunsMillis", 10));
		config.lifo = getProperty(props, "lifo", false);
		
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
