package net.oschina.j2cache.hibernate5.regions;

import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;

import java.util.Properties;

public class J2CacheCollectionRegion extends J2CacheTransactionalDataRegion implements CollectionRegion {

    public J2CacheCollectionRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion underlyingCache, SessionFactoryOptions settings, CacheDataDescription metadata, Properties properties) {
        super(accessStrategyFactory, underlyingCache, settings, metadata, properties);
    }

    @Override
    public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return this.getAccessStrategyFactory().createCollectionRegionAccessStrategy(this, accessType);
    }

}
