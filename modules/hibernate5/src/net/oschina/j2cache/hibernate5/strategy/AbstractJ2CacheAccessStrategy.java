package net.oschina.j2cache.hibernate5.strategy;

import net.oschina.j2cache.hibernate5.regions.J2CacheTransactionalDataRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.spi.SharedSessionContractImplementor;


abstract class AbstractJ2CacheAccessStrategy<T extends J2CacheTransactionalDataRegion> {

    private final T region;
    private final SessionFactoryOptions settings;

    AbstractJ2CacheAccessStrategy(T region, SessionFactoryOptions settings) {
        this.region = region;
        this.settings = settings;
    }

    protected T region() {
        return this.region;
    }

    protected SessionFactoryOptions settings() {
        return this.settings;
    }

    public final boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version) throws CacheException {
        return putFromLoad( session, key, value, txTimestamp, version, settings.isMinimalPutsEnabled() );
    }

    public abstract boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException;

    public final SoftLock lockRegion() {
        return null;
    }

    public final void unlockRegion(SoftLock lock) throws CacheException {
        region.clear();
    }

    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
    }

    public final void removeAll() throws CacheException {
        region.clear();
    }

    public final void evict(Object key) throws CacheException {
        region.remove( key );
    }

    public final void evictAll() throws CacheException {
        region.clear();
    }

}
