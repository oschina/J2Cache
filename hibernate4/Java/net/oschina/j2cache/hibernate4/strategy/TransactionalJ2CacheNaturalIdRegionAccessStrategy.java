package net.oschina.j2cache.hibernate4.strategy;

import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.hibernate4.CacheRegion;
import net.oschina.j2cache.hibernate4.regions.J2CacheNaturalIdRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

public class TransactionalJ2CacheNaturalIdRegionAccessStrategy extends AbstractJ2CacheAccessStrategy<J2CacheNaturalIdRegion> implements NaturalIdRegionAccessStrategy {

    private final CacheRegion cache;

    public TransactionalJ2CacheNaturalIdRegionAccessStrategy(J2CacheNaturalIdRegion region, CacheRegion cache, Settings settings) {
        super(region, settings);
        this.cache = cache;
    }

    @Override
    public boolean afterInsert(Object key, Object value) {
        return false;
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) {
        return false;
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        final CacheObject element = cache.get(key);
        return element == null ? null : element.getValue();
    }

    @Override
    public NaturalIdRegion getRegion() {
        return region();
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        cache.put(key, value);
        return true;
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && cache.get(key) != null) {
            return false;
        }
        cache.put(key, value);
        return true;
    }

    @Override
    public void remove(Object key) throws CacheException {
        cache.evict(key);
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        cache.put(key, value);
        return true;
    }
}
