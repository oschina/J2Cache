package net.oschina.j2cache.hibernate5.regions;

import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;

import java.util.Properties;

public class J2CacheTransactionalDataRegion extends J2CacheDataRegion implements TransactionalDataRegion {

    private static final int LOCAL_LOCK_PROVIDER_CONCURRENCY = 128;

    private final SessionFactoryOptions settings;

    protected final CacheDataDescription metadata;

    J2CacheTransactionalDataRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion cache, SessionFactoryOptions settings, CacheDataDescription metadata, Properties properties) {
        super(accessStrategyFactory, cache, properties);
        this.settings = settings;
        this.metadata = metadata;
    }

    public SessionFactoryOptions getSettings() {
        return this.settings;
    }

    @Override
    public boolean isTransactionAware() {
        return false;
    }

    @Override
    public CacheDataDescription getCacheDataDescription() {
        return this.metadata;
    }

    public final Object get(Object key) {
        CacheObject object = this.getCache().get(key);
        return object != null ? object.getValue() : null;
    }

    public final void put(Object key, Object value) throws CacheException {
        try {
            this.getCache().put(key, value);
        } catch (IllegalArgumentException e) {
            throw new CacheException(e);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

    public final void remove(Object key) throws CacheException {
        try {
            this.getCache().evict(key);
        } catch (ClassCastException e) {
            throw new CacheException(e);
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

    public final void clear() throws CacheException {
        try {
            this.getCache().clear();
        } catch (IllegalStateException e) {
            throw new CacheException(e);
        }
    }

    public final void writeLock(Object key) throws CacheException {

    }

    public final void writeUnlock(Object key) throws CacheException {

    }

    public final void readLock(Object key) throws CacheException {

    }

    public final void readUnlock(Object key) throws CacheException {

    }

    public final boolean locksAreIndependentOfCache() {
        return false;
    }

}
