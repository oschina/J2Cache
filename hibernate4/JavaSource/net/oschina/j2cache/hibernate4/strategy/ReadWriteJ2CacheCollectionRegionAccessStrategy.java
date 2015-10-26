package net.oschina.j2cache.hibernate4.strategy;


import net.oschina.j2cache.hibernate4.regions.J2CacheCollectionRegion;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

public class ReadWriteJ2CacheCollectionRegionAccessStrategy extends AbstractReadWriteJ2CacheAccessStrategy<J2CacheCollectionRegion> implements CollectionRegionAccessStrategy {

    public ReadWriteJ2CacheCollectionRegionAccessStrategy(J2CacheCollectionRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public CollectionRegion getRegion() {
        return region();
    }

}
