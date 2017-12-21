package net.oschina.j2cache.ehcache;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheExpiredListener;
import org.ehcache.event.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ehcache 3.x 的缓存封装
 * @author winterlau
 */
public class EhCache3 implements Cache , CacheEventListener {

    private String name;
    private org.ehcache.Cache<Serializable, Serializable> cache;
    private CacheExpiredListener listener;

    public EhCache3(String name, org.ehcache.Cache<Serializable, Serializable> cache, CacheExpiredListener listener) {
        this.name = name;
        this.cache = cache;
        this.cache.getRuntimeConfiguration().registerCacheEventListener(this,
                EventOrdering.ORDERED,
                EventFiring.ASYNCHRONOUS,
                EventType.EXPIRED);
        this.listener = listener;
    }

    @Override
    public Serializable get(Serializable key) throws IOException {
        return this.cache.get(key);
    }

    @Override
    public void put(Serializable key, Serializable value) throws IOException {
        this.cache.put(key, value);
    }

    @Override
    public void update(Serializable key, Serializable value) throws IOException {
        this.cache.put(key, value);
    }

    @Override
    public Set<Serializable> keys() throws IOException {
        return null;
    }

    @Override
    public void evict(Serializable key) throws IOException {
        this.cache.remove(key);
    }

    @Override
    public void evicts(List<Serializable> keys) throws IOException {
        this.cache.removeAll(keys.stream().collect(Collectors.toSet()));
    }

    @Override
    public void clear() throws IOException {
        this.cache.clear();
    }

    @Override
    public void onEvent(CacheEvent cacheEvent) {
        if(cacheEvent.getType() == EventType.EXPIRED){
            this.listener.notifyElementExpired(name, (Serializable)cacheEvent.getKey());
        }
    }
}
