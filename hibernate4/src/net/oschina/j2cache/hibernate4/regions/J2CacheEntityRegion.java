package net.oschina.j2cache.hibernate4.regions;

import net.oschina.j2cache.hibernate4.CacheRegion;
import net.oschina.j2cache.hibernate4.strategy.J2CacheAccessStrategyFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import java.util.Properties;

public class J2CacheEntityRegion extends J2CacheTransactionalDataRegion implements EntityRegion {

    public J2CacheEntityRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion underlyingCache, Settings settings, CacheDataDescription metadata, Properties properties) {
        super(accessStrategyFactory,underlyingCache, settings, metadata, properties);
    }

    @Override
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return getAccessStrategyFactory().createEntityRegionAccessStrategy( this, accessType );
    }

}
