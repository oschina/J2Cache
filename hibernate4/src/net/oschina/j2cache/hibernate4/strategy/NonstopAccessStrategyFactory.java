package net.oschina.j2cache.hibernate4.strategy;

import net.oschina.j2cache.hibernate4.nonstop.HibernateNonstopCacheExceptionHandler;
import net.oschina.j2cache.hibernate4.nonstop.NonstopAwareCollectionRegionAccessStrategy;
import net.oschina.j2cache.hibernate4.nonstop.NonstopAwareEntityRegionAccessStrategy;
import net.oschina.j2cache.hibernate4.nonstop.NonstopAwareNaturalIdRegionAccessStrategy;
import net.oschina.j2cache.hibernate4.regions.J2CacheCollectionRegion;
import net.oschina.j2cache.hibernate4.regions.J2CacheEntityRegion;
import net.oschina.j2cache.hibernate4.regions.J2CacheNaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;


public class NonstopAccessStrategyFactory implements J2CacheAccessStrategyFactory {

    private final J2CacheAccessStrategyFactory actualFactory;

    public NonstopAccessStrategyFactory(J2CacheAccessStrategyFactory actualFactory) {
        this.actualFactory = actualFactory;
    }

    @Override
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(J2CacheEntityRegion entityRegion, AccessType accessType) {
        return new NonstopAwareEntityRegionAccessStrategy(actualFactory.createEntityRegionAccessStrategy(entityRegion, accessType), HibernateNonstopCacheExceptionHandler.getInstance());
    }

    @Override
    public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion naturalIdRegion, AccessType accessType) {
        return new NonstopAwareNaturalIdRegionAccessStrategy(actualFactory.createNaturalIdRegionAccessStrategy(naturalIdRegion, accessType), HibernateNonstopCacheExceptionHandler.getInstance());
    }

    @Override
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(J2CacheCollectionRegion collectionRegion, AccessType accessType) {
        return new NonstopAwareCollectionRegionAccessStrategy(actualFactory.createCollectionRegionAccessStrategy(collectionRegion, accessType), HibernateNonstopCacheExceptionHandler.getInstance());
    }

}
