/**
 * Copyright (c) 2015-2017, liao
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.hibernate3;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.CacheObject;
import org.hibernate.HibernateException;
import org.hibernate.cache.Cache;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.Timestamper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * @author liao
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
        try {
            cache.clear(region);
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void destroy() throws CacheException {
        cache.close();
    }
    
    @Override
    public Object get(Object key) throws CacheException {
        try {
            CacheObject cobj = cache.get(region, (Serializable)key);
            if (log.isDebugEnabled())
                log.debug("get value for j2cache which key:" + key + ",value:" + cobj.getValue());
            return cobj.getValue();
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public long getElementCountInMemory() {
        try {
            int size = cache.keys(region).size();
            log.info("getElementCountInMemory which size" + size);
            return size;
        } catch (IOException e) {
            throw new CacheException(e);
        }
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
        try {
            if (log.isDebugEnabled()) {
                log.debug("set put the key to j2cache which key:" + key);
            }
            cache.set(region, (Serializable)key, (Serializable)value);
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

    @Override
    public Object read(Object key) throws CacheException {
        return this.get(key);
    }

    @Override
    public void remove(Object key) throws CacheException {
        try {
            cache.evict(region, (Serializable) key);
        } catch (IOException e) {
            throw new CacheException(e);
        }
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
