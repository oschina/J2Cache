package net.oschina.j2cache.hibernate4.regions;

import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.hibernate4.CacheRegion;
import net.oschina.j2cache.hibernate4.log.J2CacheMessageLogger;
import net.oschina.j2cache.hibernate4.strategy.J2CacheAccessStrategyFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.GeneralDataRegion;
import org.jboss.logging.Logger;

import java.util.Properties;

public class J2CacheGeneralDataRegion extends J2CacheDataRegion implements GeneralDataRegion {

    private static final J2CacheMessageLogger LOG = Logger.getMessageLogger(J2CacheMessageLogger.class, J2CacheGeneralDataRegion.class.getName());

    public J2CacheGeneralDataRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion underlyingCache, Properties properties) {
        super(accessStrategyFactory, underlyingCache, properties);
    }

    @Override
    public Object get(Object key) throws CacheException {
        LOG.debugf("key: %s", key);
        if (key == null) {
            return null;
        } else {
            CacheObject value = getCache().get(key);
            if (value == null) {
                LOG.debugf("value for key %s is null", key);
                return null;
            } else {
                return value.getValue();
            }
        }
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        LOG.debugf("key: %s value: %s", key, value);
        try {
            getCache().put(key, value);
        } catch (IllegalArgumentException e) {
            throw new CacheException(e);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void evict(Object key) throws CacheException {
        try {
            getCache().evict(key);
        } catch (ClassCastException e) {
            throw new CacheException(e);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void evictAll() throws CacheException {
        try {
            getCache().clear();
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }
}