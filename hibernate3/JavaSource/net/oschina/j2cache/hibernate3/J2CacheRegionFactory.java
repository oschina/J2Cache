/**
 *
 */
package net.oschina.j2cache.hibernate3;

import net.oschina.j2cache.CacheChannel;
import org.hibernate.cache.*;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * J2Cache Hibernate RegionFactory implementations.
 *
 * @author winterlau
 */
public class J2CacheRegionFactory implements RegionFactory {

    private CacheChannel channel = CacheChannel.getInstance();

    @Override
    public CollectionRegion buildCollectionRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheRegion.Collection(regionName, channel);
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheRegion.Entity(regionName, channel);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        return new J2CacheRegion.QueryResults(regionName, channel);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        return new J2CacheRegion.Timestamps(regionName, channel);
    }

    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {

    }

    @Override
    public void stop() {
        channel.close();
    }

}
