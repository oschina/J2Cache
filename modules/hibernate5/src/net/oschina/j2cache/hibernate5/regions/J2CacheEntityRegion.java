package net.oschina.j2cache.hibernate5.regions;

import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;

import java.util.Properties;

public class J2CacheEntityRegion extends J2CacheTransactionalDataRegion implements EntityRegion {

    public J2CacheEntityRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion underlyingCache, SessionFactoryOptions settings, CacheDataDescription metadata, Properties properties) {
        super(accessStrategyFactory,underlyingCache, settings, metadata, properties);
    }

    @Override
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return this.getAccessStrategyFactory().createEntityRegionAccessStrategy( this, accessType );
    }

}
