package net.oschina.j2cache.hibernate4;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.hibernate4.regions.*;
import net.oschina.j2cache.hibernate4.strategy.J2CacheAccessStrategyFactory;
import net.oschina.j2cache.hibernate4.strategy.J2CacheAccessStrategyFactoryImpl;
import net.oschina.j2cache.hibernate4.strategy.NonstopAccessStrategyFactory;
import net.oschina.j2cache.hibernate4.util.Timestamper;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;

import java.util.Properties;

abstract class AbstractJ2CacheRegionFactory implements RegionFactory {

    protected Settings settings;

    protected CacheChannel channel;

    protected final J2CacheAccessStrategyFactory accessStrategyFactory = new NonstopAccessStrategyFactory(new J2CacheAccessStrategyFactoryImpl());

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheEntityRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheNaturalIdRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheCollectionRegion(accessStrategyFactory, getCache(regionName), settings, metadata, properties);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new J2CacheQueryResultsRegion(accessStrategyFactory, getCache(regionName), properties);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new J2CacheTimestampsRegion(accessStrategyFactory, getCache(regionName), properties);
    }

    private CacheRegion getCache(String name) throws CacheException {
        return new J2CacheCacheRegion(channel,name);
    }

    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

    public void setChannel(CacheChannel channel) {
        this.channel = channel;
    }
}

