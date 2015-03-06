package net.oschina.j2cache.hibernate4;

import org.hibernate.HibernateException;
import org.hibernate.cache.spi.QueryCache;
import org.hibernate.cache.spi.QueryCacheFactory;
import org.hibernate.cache.spi.UpdateTimestampsCache;
import org.hibernate.cfg.Settings;

import java.util.Properties;

public class J2CacheQueryCacheFactory implements QueryCacheFactory {

    @Override
    public QueryCache getQueryCache(
            final String regionName,
            final UpdateTimestampsCache updateTimestampsCache,
            final Settings settings,
            final Properties props) throws HibernateException {
        return new J2CacheQueryCache(settings, props, updateTimestampsCache, regionName);
    }

}