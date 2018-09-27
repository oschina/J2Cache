package net.oschina.j2cache.hibernate5.strategy;

import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheNaturalIdRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class TransactionalJ2CacheNaturalIdRegionAccessStrategy extends AbstractJ2CacheAccessStrategy<J2CacheNaturalIdRegion> implements NaturalIdRegionAccessStrategy {

    private final CacheRegion cache;

    public TransactionalJ2CacheNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion region, CacheRegion cache, SessionFactoryOptions settings) {
        super(region, settings);
        this.cache = cache;
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value) {
        return false;
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, SoftLock lock) {
        return false;
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
        final CacheObject element = cache.get(key);
        return element == null ? null : element.getValue();
    }

    @Override
    public NaturalIdRegion getRegion() {
        return region();
    }

    @Override
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        cache.put(key, value);
        return true;
    }

    @Override
    public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && cache.get(key) != null) {
            return false;
        }
        cache.put(key, value);
        return true;
    }

    @Override
    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
        cache.evict(key);
    }

    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
    }

    @Override
    public boolean update(SharedSessionContractImplementor session, Object key, Object value) throws CacheException {
        cache.put(key, value);
        return true;
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
