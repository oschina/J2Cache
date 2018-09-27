package net.oschina.j2cache.hibernate5.strategy;


import net.oschina.j2cache.hibernate5.regions.J2CacheNaturalIdRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class NonStrictReadWriteJ2CacheNaturalIdRegionAccessStrategy extends AbstractJ2CacheAccessStrategy<J2CacheNaturalIdRegion> implements NaturalIdRegionAccessStrategy {

    public NonStrictReadWriteJ2CacheNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion region, SessionFactoryOptions settings) {
        super(region, settings);
    }

    @Override
    public NaturalIdRegion getRegion() {
        return region();
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
        return region().get(key);
    }

    @Override
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && region().contains(key)) {
            return false;
        } else {
            region().put(key, value);
            return true;
        }
    }

    @Override
    public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
        region().remove(key);
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        remove(session, key);
        return false;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) throws CacheException {
        unlockItem(session, key, lock);
        return false;
    }

    @Override
    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
        region().remove(key);
    }

    @Override
    public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SharedSessionContractImplementor session) {
        return DefaultCacheKeysFactory.staticCreateNaturalIdKey(naturalIdValues, persister, session);
    }

    @Override
    public Object[] getNaturalIdValues(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetNaturalIdValues( cacheKey );
    }

}
