package net.oschina.j2cache.hibernate4.strategy;

import net.oschina.j2cache.hibernate4.regions.J2CacheEntityRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

public class ReadOnlyJ2CacheEntityRegionAccessStrategy extends AbstractJ2CacheAccessStrategy<J2CacheEntityRegion> implements EntityRegionAccessStrategy {

    public ReadOnlyJ2CacheEntityRegionAccessStrategy(J2CacheEntityRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public EntityRegion getRegion() {
        return region();
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        return region().get(key);
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException {
        if (minimalPutOverride && region().contains(key)) {
            return false;
        } else {
            region().put(key, value);
            return true;
        }
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        evict(key);
    }

    @Override
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        region().put(key, value);
        return true;
    }

    @Override
    public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }

    @Override
    public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Can't write to a readonly object");
    }

}
