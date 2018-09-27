package net.oschina.j2cache.hibernate5.strategy;


import net.oschina.j2cache.hibernate5.regions.J2CacheCollectionRegion;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class ReadWriteJ2CacheCollectionRegionAccessStrategy extends AbstractReadWriteJ2CacheAccessStrategy<J2CacheCollectionRegion> implements CollectionRegionAccessStrategy {

    public ReadWriteJ2CacheCollectionRegionAccessStrategy(J2CacheCollectionRegion region, SessionFactoryOptions settings) {
        super(region, settings);
    }

    @Override
    public CollectionRegion getRegion() {
        return region();
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
