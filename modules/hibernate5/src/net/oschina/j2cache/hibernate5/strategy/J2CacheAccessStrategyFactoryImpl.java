package net.oschina.j2cache.hibernate5.strategy;


import net.oschina.j2cache.hibernate5.log.J2CacheMessageLogger;
import net.oschina.j2cache.hibernate5.regions.J2CacheCollectionRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheEntityRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheNaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.jboss.logging.Logger;

public class J2CacheAccessStrategyFactoryImpl implements J2CacheAccessStrategyFactory {

    private static final J2CacheMessageLogger LOG = Logger.getMessageLogger(J2CacheMessageLogger.class, J2CacheAccessStrategyFactoryImpl.class.getName());

    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(J2CacheEntityRegion entityRegion, AccessType accessType) {
        switch (accessType) {
            case READ_ONLY:
                if (entityRegion.getCacheDataDescription().isMutable()) {
                    LOG.readOnlyCacheConfiguredForMutableEntity(entityRegion.getName());
                }
                return new ReadOnlyJ2CacheEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteJ2CacheEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteJ2CacheEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalJ2CacheEntityRegionAccessStrategy(entityRegion, entityRegion.getJ2Cache(), entityRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");

        }

    }

    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(J2CacheCollectionRegion collectionRegion, AccessType accessType) {
        switch (accessType) {
            case READ_ONLY:
                if (collectionRegion.getCacheDataDescription().isMutable()) {
                    LOG.readOnlyCacheConfiguredForMutableEntity(collectionRegion.getName());
                }
                return new ReadOnlyJ2CacheCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteJ2CacheCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteJ2CacheCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalJ2CacheCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getJ2Cache(), collectionRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    @Override
    public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion naturalIdRegion, AccessType accessType) {
        switch (accessType) {
            case READ_ONLY:
                if (naturalIdRegion.getCacheDataDescription().isMutable()) {
                    LOG.readOnlyCacheConfiguredForMutableEntity(naturalIdRegion.getName());
                }
                return new ReadOnlyJ2CacheNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteJ2CacheNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteJ2CacheNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalJ2CacheNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getJ2Cache(), naturalIdRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

}
