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

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>EHCache 3.x 的缓存封装</p>
 * <p>该封装类实现了缓存操作以及对缓存数据失效的侦听</p>
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class EhCache3 implements Cache , CacheEventListener {

    private String name;
    private org.ehcache.Cache<String, Serializable> cache;
    private CacheExpiredListener listener;

    public EhCache3(String name, org.ehcache.Cache<String, Serializable> cache, CacheExpiredListener listener) {
        this.name = name;
        this.cache = cache;
        this.cache.getRuntimeConfiguration().registerCacheEventListener(this,
                EventOrdering.ORDERED,
                EventFiring.ASYNCHRONOUS,
                EventType.EXPIRED);
        this.listener = listener;
    }

    @Override
    public Serializable get(String key) {
        return this.cache.get(key);
    }

    @Override
    public void put(String key, Serializable value) {
        this.cache.put(key, value);
    }

    @Override
    public Map getAll(Collection<String> keys) {
        return cache.getAll(keys.stream().collect(Collectors.toSet()));
    }

    @Override
    public boolean exists(String key) {
        return cache.containsKey(key);
    }

    @Override
    public Serializable putIfAbsent(String key, Serializable value) {
        return cache.putIfAbsent(key, value);
    }

    @Override
    public void putAll(Map<String, Serializable> elements) {
        cache.putAll(elements);
    }

    @Override
    public Collection<String> keys() {
        return null;
    }

    @Override
    public void evict(String...keys) {
        this.cache.removeAll(Arrays.asList(keys).stream().collect(Collectors.toSet()));
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public void onEvent(CacheEvent cacheEvent) {
        if(cacheEvent.getType() == EventType.EXPIRED){
            this.listener.notifyElementExpired(name, (String)cacheEvent.getKey());
        }
    }
}
