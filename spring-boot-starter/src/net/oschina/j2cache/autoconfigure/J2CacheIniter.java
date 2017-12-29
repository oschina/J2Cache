package net.oschina.j2cache.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

public class J2CacheIniter {
    private static Logger logger = LoggerFactory.getLogger(J2CacheIniter.class);
    private final J2CacheConfig j2CacheConfig;

    public J2CacheIniter(J2CacheConfig j2CacheConfig) {
        this.j2CacheConfig = j2CacheConfig;
    }

    @PostConstruct
    public void init() {
    }
}
