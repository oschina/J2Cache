package net.oschina.j2cache.hibernate5.strategy;

import net.oschina.j2cache.hibernate5.regions.J2CacheEntityRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;


public class ReadWriteJ2CacheEntityRegionAccessStrategy extends AbstractReadWriteJ2CacheAccessStrategy<J2CacheEntityRegion> implements EntityRegionAccessStrategy {

    public ReadWriteJ2CacheEntityRegionAccessStrategy(J2CacheEntityRegion region, SessionFactoryOptions settings) {
        super( region, settings );
    }

    @Override
    public EntityRegion getRegion() {
        return region();
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
        region().writeLock( key );
        try {
            final AbstractReadWriteJ2CacheAccessStrategy.Lockable item = (AbstractReadWriteJ2CacheAccessStrategy.Lockable) region().get( key );
            if ( item == null ) {
                region().put( key, new AbstractReadWriteJ2CacheAccessStrategy.Item( value, version, region().nextTimestamp() ) );
                return true;
            }
            else {
                return false;
            }
        }
        finally {
            region().writeUnlock( key );
        }
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion)
            throws CacheException {
        return false;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
            throws CacheException {
        //what should we do with previousVersion here?
        region().writeLock( key );
        try {
            final AbstractReadWriteJ2CacheAccessStrategy.Lockable item = (AbstractReadWriteJ2CacheAccessStrategy.Lockable) region().get( key );

            if ( item != null && item.isUnlockable( lock ) ) {
                final AbstractReadWriteJ2CacheAccessStrategy.Lock lockItem = (AbstractReadWriteJ2CacheAccessStrategy.Lock) item;
                if ( lockItem.wasLockedConcurrently() ) {
                    decrementLock( key, lockItem );
                    return false;
                }
                else {
                    region().put( key, new AbstractReadWriteJ2CacheAccessStrategy.Item( value, currentVersion, region().nextTimestamp() ) );
                    return true;
                }
            }
            else {
                handleLockExpiry( key, item );
                return false;
            }
        }
        finally {
            region().writeUnlock( key );
        }
    }

    @Override
    public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.staticCreateEntityKey(id, persister, factory, tenantIdentifier);
    }

    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetEntityId(cacheKey);
    }

}
