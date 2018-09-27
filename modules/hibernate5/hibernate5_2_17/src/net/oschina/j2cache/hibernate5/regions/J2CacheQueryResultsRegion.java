package net.oschina.j2cache.hibernate5.regions;

import net.oschina.j2cache.hibernate5.CacheRegion;
import net.oschina.j2cache.hibernate5.strategy.J2CacheAccessStrategyFactory;
import org.hibernate.cache.spi.QueryResultsRegion;

import java.util.Properties;

public class J2CacheQueryResultsRegion extends J2CacheGeneralDataRegion implements QueryResultsRegion {

    public J2CacheQueryResultsRegion(J2CacheAccessStrategyFactory accessStrategyFactory, CacheRegion underlyingCache, Properties properties) {
        super( accessStrategyFactory, underlyingCache, properties );
    }

}