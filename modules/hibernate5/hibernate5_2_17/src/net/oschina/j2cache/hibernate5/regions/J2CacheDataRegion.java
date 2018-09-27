package net.oschina.j2cache.hibernate5.regions;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.oschina.j2cache.hibernate5.log.J2CacheMessageLogger;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.Region;

import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;
import net.oschina.j2cache.hibernate5.util.Timestamper;

public abstract class J2CacheDataRegion implements Region {

    private static final J2CacheMessageLogger LOG = org.jboss.logging.Logger.getMessageLogger(
            J2CacheMessageLogger.class,
            J2CacheDataRegion.class.getName()
    );
    private static final String CACHE_LOCK_TIMEOUT_PROPERTY = "hibernate.cache_lock_timeout";
    private static final int DEFAULT_CACHE_LOCK_TIMEOUT = 60000;

    private final CacheRegion cache;
    private final J2CacheAccessStrategyFactory accessStrategyFactory;
    private final int cacheLockTimeout;

    J2CacheDataRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion cache, Properties properties) {
        this.accessStrategyFactory = accessStrategyFactory;
        this.cache = cache;
        String timeout = properties.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY, Integer.toString(DEFAULT_CACHE_LOCK_TIMEOUT));
        this.cacheLockTimeout = Timestamper.ONE_MS * Integer.decode(timeout);
    }

    protected CacheRegion getCache() {
        return this.cache;
    }

    public CacheRegion getJ2Cache() {
        return this.getCache();
    }

    protected J2CacheAccessStrategyFactory getAccessStrategyFactory() {
        return this.accessStrategyFactory;
    }

    public String getName() {
        return this.getCache().getName();
    }

    public void destroy() throws CacheException {
        try {
            this.getCache().clear();
        } catch (IllegalStateException e) {
            LOG.debug("This can happen if multiple frameworks both try to shutdown ehcache", e);
        }
    }

    public long getSizeInMemory() {
        return -1;
    }

    @Override
    public long getElementCountInMemory() {
        return -1;
    }

    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    @Override
    public Map toMap() {
        try {
            Map<Object, Object> result = new HashMap<Object, Object>();
            for (Object key : this.cache.keys()) {
                CacheObject e = this.cache.get(key);
                if (e != null) {
                    result.put(key, e.getValue());
                }
            }
            return result;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public int getTimeout() {
        return this.cacheLockTimeout;
    }

    @Override
    public boolean contains(Object key) {
        return false;
    }

}