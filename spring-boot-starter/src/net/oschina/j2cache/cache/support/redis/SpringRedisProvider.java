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
import net.oschina.j2cache.cache.support.until.SpringUtil;

public class SpringRedisProvider implements CacheProvider{
	
	private final static Logger log = LoggerFactory.getLogger(SpringRedisProvider.class);
	
	@Autowired
	private RedisTemplate<String, Serializable> redisTemplate;
	
    private String namespace;
    
    protected ConcurrentHashMap<String, SpringRedisCache> caches = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
	public SpringRedisProvider() {
    	this.redisTemplate = SpringUtil.getBean(RedisTemplate.class);
    }
    
	
	@Override
	public String name() {
		 return "spring-redis";
	}

	@Override
	public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
        SpringRedisCache cache = caches.get(regionName);
        if (cache == null) {
            synchronized(SpringRedisProvider.class) {
                if(cache == null) {
                    cache = new SpringRedisCache(this.namespace, regionName, redisTemplate);
                    caches.put(regionName, cache);
                }
            }
        }
        return cache;
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
