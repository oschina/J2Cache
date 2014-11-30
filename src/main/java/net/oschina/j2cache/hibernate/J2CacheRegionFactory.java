/**
 * 
 */
package net.oschina.j2cache.hibernate;

import java.util.Properties;

import net.oschina.j2cache.CacheChannel;

import org.hibernate.cache.CacheDataDescription;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.CollectionRegion;
import org.hibernate.cache.EntityRegion;
import org.hibernate.cache.QueryResultsRegion;
import org.hibernate.cache.RegionFactory;
import org.hibernate.cache.Timestamper;
import org.hibernate.cache.TimestampsRegion;
import org.hibernate.cfg.Settings;

/**
 * J2Cache Hibernate RegionFactory implementations.
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
