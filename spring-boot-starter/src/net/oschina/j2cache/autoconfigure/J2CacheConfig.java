package net.oschina.j2cache.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * 相关的配置信息
 * @author 小雨哥哥
 *
 */
@ConfigurationProperties(prefix = "j2cache")
public class J2CacheConfig {
    private String configLocation = "/j2cache.properties";
    
    /**
     * 是否开启spring cache缓存,注意:开启后需要添加spring.cache.type=none,将缓存类型设置为none
     */
    private Boolean openSpringCache = false;

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

	public Boolean getOpenSpringCache() {
		return openSpringCache;
	}

	public void setOpenSpringCache(Boolean openSpringCache) {
		this.openSpringCache = openSpringCache;
	}
}
