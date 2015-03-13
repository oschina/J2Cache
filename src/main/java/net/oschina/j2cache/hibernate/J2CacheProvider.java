/**
 * 
 */
package net.oschina.j2cache.hibernate;

import java.util.Properties;

import net.oschina.j2cache.CacheChannel;

import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CacheProvider;
import org.hibernate.cache.Timestamper;

/**
 * @author liao
 *
 */
@SuppressWarnings("deprecation")
public class J2CacheProvider implements CacheProvider {

    @Override
    public Cache buildCache(String regionName, Properties properties)
            throws CacheException {
        return new J2HibernateCache(regionName, CacheChannel.getInstance());
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
        CacheChannel.getInstance().close();
    }

}
