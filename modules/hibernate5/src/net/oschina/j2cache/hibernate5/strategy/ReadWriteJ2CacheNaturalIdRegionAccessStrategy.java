package net.oschina.j2cache.hibernate5.strategy;

import net.oschina.j2cache.hibernate5.regions.J2CacheNaturalIdRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class ReadWriteJ2CacheNaturalIdRegionAccessStrategy extends AbstractReadWriteJ2CacheAccessStrategy<J2CacheNaturalIdRegion> implements NaturalIdRegionAccessStrategy {

    public ReadWriteJ2CacheNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion region, SessionFactoryOptions settings) {
        super(region, settings);
    }

    @Override
    public NaturalIdRegion getRegion() {
        return region();
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        region().writeLock(key);
        try {
            final Lockable item = (Lockable) region().get(key);
            if (item == null) {
                region().put(key, new Item(value, null, region().nextTimestamp()));
                return true;
            } else {
                return false;
            }
        } finally {
            region().writeUnlock(key);
        }
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) throws CacheException {
        region().writeLock(key);
        try {
            final Lockable item = (Lockable) region().get(key);
            if (item != null && item.isUnlockable(lock)) {
                final Lock lockItem = (Lock) item;
                if (lockItem.wasLockedConcurrently()) {
                    decrementLock(key, lockItem);
                    return false;
                } else {
                    region().put(key, new Item(value, null, region().nextTimestamp()));
                    return true;
                }
            } else {
                handleLockExpiry(key, item);
                return false;
            }
        } finally {
            region().writeUnlock(key);
        }
    }

    @Override
    public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
        return DefaultCacheKeysFactory.staticCreateNaturalIdKey(naturalIdValues, persister, session);
    }

    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetNaturalIdValues(cacheKey);
    }
}