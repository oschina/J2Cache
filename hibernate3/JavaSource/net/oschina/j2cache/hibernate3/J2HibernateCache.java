/**
 * 
 */
package net.oschina.j2cache.hibernate3;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author liao
 *
 */
@SuppressWarnings("deprecation")
public class J2HibernateCache implements Cache {
    
    private final static Logger log = LoggerFactory.getLogger(J2HibernateCache.class);
    
    private String region;
    
    private CacheChannel cache;

    public J2HibernateCache(String region, CacheChannel cache) {
        super();
        this.region = region;
        this.cache = cache;
    }

    @Override
    public void clear() throws CacheException {
        cache.clear(region);
    }

    @Override
    public void destroy() throws CacheException {
        cache.close();
    }
    
    @Override
    public Object get(Object key) throws CacheException {
        CacheObject cobj = cache.get(region, key);
        if (log.isDebugEnabled()) {
            log.debug("get value for j2cache which key:" + key + ",value:" + cobj.getValue());
        }
        return cobj.getValue();
    }

    @Override
    public long getElementCountInMemory() {
        int size = cache.keys(region).size();
        log.info("getElementCountInMemory which size" + size);
        return size;
    }

    @Override
    public long getElementCountOnDisk() {
        return -1L;
    }

    @Override
    public String getRegionName() {
        return region;
    }

    @Override
    public long getSizeInMemory() {
        return -1L;
    }

    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public void lock(Object key) throws CacheException {
        // do nothing
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        if (log.isDebugEnabled()) {
            log.debug("set put the key to j2cache which key:" + key);
        }
        cache.set(region, key, value);
    }

    @Override
    public Object read(Object key) throws CacheException {
        return this.get(key);
    }

    @Override
    public void remove(Object key) throws CacheException {
        log.info("remove key from j2cache which key:" + key);
        cache.evict(region, key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map toMap() {
        // There is a optional implement method
        // 1. get all keys in region, use CacheChannel.getInstance().keys(region)
        // 2. for loop the keys to get the value
        // but the method is inefficiency when the keys are in redis, maybe support batchGet(List keys) method is better? :)
        throw new UnsupportedOperationException("j2cache not support region to map");
    }

    @Override
    public void unlock(Object key) throws CacheException {

    }

    @Override
    public void update(Object key, Object value) throws CacheException {
        this.put(key, value);
    }

}
