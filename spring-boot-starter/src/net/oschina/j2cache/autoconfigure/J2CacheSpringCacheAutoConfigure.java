package net.oschina.j2cache.autoconfigure;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.cache.support.J2CacheCacheManger;

/**
 * 
 * @author zhangsaizz
 *
 */
@Configuration
@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({J2CacheConfig.class, CacheProperties.class})
@ConditionalOnProperty(name = "j2cache.open-spring-cache", havingValue = "true")
@AutoConfigureAfter(value = J2CacheAutoConfigure.class)
@EnableCaching
public class J2CacheSpringCacheAutoConfigure {
	
	
	private final CacheProperties cacheProperties;
	
	J2CacheSpringCacheAutoConfigure(CacheProperties cacheProperties){
		this.cacheProperties = cacheProperties;
	}

    @Bean
    public J2CacheCacheManger cacheManager() {
    	List<String> cacheNames = cacheProperties.getCacheNames();
    	J2CacheCacheManger cacheCacheManger = new J2CacheCacheManger();
    	cacheCacheManger.setCacheNames(cacheNames);
    	return cacheCacheManger;
    }
    
}
