package net.oschina.j2cache.autoconfigure;

import net.oschina.j2cache.J2Cache;
import net.oschina.j2cache.cache.support.util.SpringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass(J2Cache.class)
@EnableConfigurationProperties({J2CacheConfig.class})
@Configuration
public class J2CacheAutoConfigure {
    private static Logger logger = LoggerFactory.getLogger(J2CacheAutoConfigure.class);

    private final J2CacheConfig j2CacheConfig;

    public J2CacheAutoConfigure(J2CacheConfig j2CacheConfig) {
        this.j2CacheConfig = j2CacheConfig;
    }

    @Bean
    @ConditionalOnMissingBean(J2CacheIniter.class)
    public J2CacheIniter j2CacheIniter() {
        return new J2CacheIniter(j2CacheConfig);
    }

    
    @Bean
    public SpringUtil springUtil() {
    	return new SpringUtil();
    }

}
