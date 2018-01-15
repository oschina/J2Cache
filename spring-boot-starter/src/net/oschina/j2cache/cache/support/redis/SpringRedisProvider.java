package net.oschina.j2cache.cache.support.redis;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.CacheProvider;
import net.oschina.j2cache.cache.support.util.SpringUtil;

/**
 * 
 * @author zhangsaizz
 *
 */
public class SpringRedisProvider implements CacheProvider{
	
	private final static Logger log = LoggerFactory.getLogger(SpringRedisProvider.class);
	
	@Autowired
	private RedisTemplate<String, Serializable> redisTemplate;
	
    private String namespace = "j2cache";
    
    protected ConcurrentHashMap<String, SpringRedisCache> caches = new ConcurrentHashMap<>();
    
	@SuppressWarnings("unchecked")
	public SpringRedisProvider() {
    	this.redisTemplate = SpringUtil.getBean("j2CacheRedisTemplate", RedisTemplate.class);
    }
    
	
	@Override
	public String name() {
		 return "redis";
	}

	@Override
	public int level() {
		return Cache.LEVEL_2;
	}

	@Override
	public Cache buildCache(String region, CacheExpiredListener listener) {
        SpringRedisCache cache = caches.get(region);
        if (cache == null) {
            synchronized(SpringRedisProvider.class) {
                if(cache == null) {
                    cache = new SpringRedisCache(this.namespace, region, redisTemplate);
                    caches.put(region, cache);
                }
            }
        }
        return cache;
	}

	@Override
	public Cache buildCache(String region, long timeToLiveInSeconds, CacheExpiredListener listener) {
		return buildCache(region, listener);
	}

	@Override
	public void start(Properties props) {
		 this.namespace = props.getProperty("namespace");
		 if(redisTemplate == null) {
			 log.error("redisTemplate 为空，请检查是否有配置spring redis!");
		 }	
	}

	@Override
	public void stop() {
		//由spring控制
	}

}
