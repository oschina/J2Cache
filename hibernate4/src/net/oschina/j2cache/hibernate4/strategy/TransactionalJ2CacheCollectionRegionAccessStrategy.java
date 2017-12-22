package net.oschina.j2cache.hibernate4.strategy;


import net.oschina.j2cache.CacheObject;
import net.oschina.j2cache.hibernate4.CacheRegion;
import net.oschina.j2cache.hibernate4.regions.J2CacheCollectionRegion;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

public class TransactionalJ2CacheCollectionRegionAccessStrategy extends AbstractJ2CacheAccessStrategy<J2CacheCollectionRegion> implements CollectionRegionAccessStrategy {

    private final CacheRegion cache;

    public TransactionalJ2CacheCollectionRegionAccessStrategy(J2CacheCollectionRegion region, CacheRegion cache, Settings settings) {
        super(region, settings);
        this.cache = cache;
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        CacheObject object = cache.get(key);
        return object != null ? object.getValue() : null;
    }

    @Override
    public CollectionRegion getRegion() {
        return region();
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

}