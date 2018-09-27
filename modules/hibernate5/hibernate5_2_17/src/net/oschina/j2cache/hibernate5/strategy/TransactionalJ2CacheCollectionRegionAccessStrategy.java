package net.oschina.j2cache.hibernate5.strategy;


import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheCollectionRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class TransactionalJ2CacheCollectionRegionAccessStrategy extends AbstractJ2CacheAccessStrategy<J2CacheCollectionRegion> implements CollectionRegionAccessStrategy {

    private final CacheRegion cache;

    public TransactionalJ2CacheCollectionRegionAccessStrategy(J2CacheCollectionRegion region, CacheRegion cache, SessionFactoryOptions settings) {
        super(region, settings);
        this.cache = cache;
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
        CacheObject object = cache.get(key);
        return object != null ? object.getValue() : null;
    }

    @Override
    public CollectionRegion getRegion() {
        return region();
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
    public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.staticCreateCollectionKey( id, persister, factory, tenantIdentifier );
    }

    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetCollectionId(cacheKey);
    }

}