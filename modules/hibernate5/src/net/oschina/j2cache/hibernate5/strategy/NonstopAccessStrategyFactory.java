package net.oschina.j2cache.hibernate5.strategy;

import net.oschina.j2cache.hibernate5.nonstop.HibernateNonstopCacheExceptionHandler;
import net.oschina.j2cache.hibernate5.nonstop.NonstopAwareCollectionRegionAccessStrategy;
import net.oschina.j2cache.hibernate5.nonstop.NonstopAwareEntityRegionAccessStrategy;
import net.oschina.j2cache.hibernate5.nonstop.NonstopAwareNaturalIdRegionAccessStrategy;
import net.oschina.j2cache.hibernate5.regions.J2CacheCollectionRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheEntityRegion;
import net.oschina.j2cache.hibernate5.regions.J2CacheNaturalIdRegion;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;
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
        return new NonstopAwareEntityRegionAccessStrategy(this.actualFactory.createEntityRegionAccessStrategy(entityRegion, accessType), HibernateNonstopCacheExceptionHandler.getInstance());
    }

    @Override
    public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion naturalIdRegion, AccessType accessType) {
        return new NonstopAwareNaturalIdRegionAccessStrategy(this.actualFactory.createNaturalIdRegionAccessStrategy(naturalIdRegion, accessType), HibernateNonstopCacheExceptionHandler.getInstance());
    }

    @Override
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(J2CacheCollectionRegion collectionRegion, AccessType accessType) {
        return new NonstopAwareCollectionRegionAccessStrategy(this.actualFactory.createCollectionRegionAccessStrategy(collectionRegion, accessType ), HibernateNonstopCacheExceptionHandler.getInstance());
    }
}
