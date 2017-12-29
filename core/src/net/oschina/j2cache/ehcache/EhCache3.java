/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
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
 * <p>EHCache 3.x 的缓存封装</p>
 * <p>该封装类实现了缓存操作以及对缓存数据失效的侦听</p>
 *
 * @author Winter Lau(javayou@gmail.com)
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
