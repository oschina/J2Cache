package net.oschina.j2cache.hibernate3;

import net.oschina.j2cache.J2Cache;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Timestamper;

import java.util.Properties;

/**
 * @author liao
 *
 */
@SuppressWarnings("deprecation")
public class J2CacheProvider implements CacheProvider {

    @Override
    public Cache buildCache(String regionName, Properties properties)
            throws CacheException {
        return new J2HibernateCache(regionName, J2Cache.getChannel());
    }

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public void start(Properties properties) throws CacheException {

    }

    @Override
    public void stop() {
        J2Cache.getChannel().close();
    }

}
