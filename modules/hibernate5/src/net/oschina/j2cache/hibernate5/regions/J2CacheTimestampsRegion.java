package net.oschina.j2cache.hibernate5.regions;

import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;
import org.hibernate.cache.spi.TimestampsRegion;

import java.util.Properties;

public class J2CacheTimestampsRegion extends J2CacheGeneralDataRegion implements TimestampsRegion {

    public J2CacheTimestampsRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion underlyingCache, Properties properties) {
        super(accessStrategyFactory, underlyingCache, properties);
    }

}
