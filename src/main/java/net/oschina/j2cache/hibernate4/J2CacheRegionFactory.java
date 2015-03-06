/**
 *
 */
package net.oschina.j2cache.hibernate4;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.hibernate4.util.Timestamper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * J2Cache Hibernate RegionFactory implementations.
 * @author winterlau
 */
public class J2CacheRegionFactory implements RegionFactory {

    private final static Log LOG = LogFactory.getLog(J2CacheRegionFactory.class);

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
    public NaturalIdRegion buildNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) throws CacheException {
        return new J2CacheNaturalIdRegion(regionName,properties,metadata);
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
    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        //TODO 可以通过hibernate settings 做一些额外的设置
        LOG.debug("settings:"+settings);
        LOG.debug("properties:"+properties);
    }

    @Override
    public void stop() {
        channel.close();
    }

}
