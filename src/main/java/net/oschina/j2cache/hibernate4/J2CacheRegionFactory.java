package net.oschina.j2cache.hibernate4;

import net.oschina.j2cache.CacheChannel;
import org.hibernate.cache.CacheException;
import org.hibernate.cfg.Settings;

import java.util.Properties;


public class J2CacheRegionFactory extends AbstractJ2CacheRegionFactory {

    private static final String SPRING_CACHEMANAGER = "hibernate.cache.spring.cache_manager";

    private static final String DEFAULT_SPRING_CACHEMANAGER = "cacheManager";

    @SuppressWarnings("UnusedDeclaration")
    public J2CacheRegionFactory() {
    }

    @SuppressWarnings("UnusedDeclaration")
    public J2CacheRegionFactory(Properties prop) {
        super();
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        this.settings = settings;
        if (this.channel == null) {
            this.channel = CacheChannel.getInstance();
        }
    }

    @Override
    public void stop() {
        if (channel != null) {
            channel.close();
        }
    }

}