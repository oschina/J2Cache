package net.oschina.j2cache.hibernate4;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

import java.util.Map;
import java.util.Properties;

public class J2CacheNaturalIdRegion implements NaturalIdRegion {

    public J2CacheNaturalIdRegion(String regionName, Properties properties, CacheDataDescription metadata) {
    }

    @Override
    public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return null;
    }

    @Override
    public boolean isTransactionAware() {
        return false;
    }

    @Override
    public CacheDataDescription getCacheDataDescription() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void destroy() throws CacheException {

    }

    @Override
    public boolean contains(Object key) {
        return false;
    }

    @Override
    public long getSizeInMemory() {
        return 0;
    }

    @Override
    public long getElementCountInMemory() {
        return 0;
    }

    @Override
    public long getElementCountOnDisk() {
        return 0;
    }

    @Override
    public Map toMap() {
        return null;
    }

    @Override
    public long nextTimestamp() {
        return 0;
    }

    @Override
    public int getTimeout() {
        return 0;
    }
}
