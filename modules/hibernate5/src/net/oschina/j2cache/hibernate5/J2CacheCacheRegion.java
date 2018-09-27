package net.oschina.j2cache.hibernate5;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;

public class J2CacheCacheRegion implements CacheRegion {

    private CacheChannel cacheChannel;
    private String region;

    public J2CacheCacheRegion(CacheChannel channel, String region) {
        this.cacheChannel = channel;
        this.region = region;
    }

    @Override
    public String getName() {
        return this.region;
    }

    @Override
    public void clear() {
        this.cacheChannel.clear(this.region);
    }

    @Override
    public CacheObject get(Object key) {
        return this.cacheChannel.get(this.region, key.toString());
    }

    @Override
    public void put(Object key, Object value) {
        this.cacheChannel.set(this.region, key.toString(), value);
    }

    @Override
    public void evict(Object key) {
        this.cacheChannel.evict(this.region, key.toString());
    }

    public Iterable<? extends Object> keys() {
        return this.cacheChannel.keys(this.region);
    }
}
