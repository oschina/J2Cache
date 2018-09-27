package net.oschina.j2cache.hibernate5;

import net.oschina.j2cache.CacheObject;

public interface CacheRegion {

    String getName();

    void clear();

    CacheObject get(Object key);

    void put(Object key, Object value);

    void evict(Object key);

    Iterable<? extends Object> keys();
}
