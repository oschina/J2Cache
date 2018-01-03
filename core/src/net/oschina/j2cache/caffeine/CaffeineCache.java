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
package net.oschina.j2cache.caffeine;

import net.oschina.j2cache.Cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Caffeine cache
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class CaffeineCache implements Cache {

    private com.github.benmanes.caffeine.cache.Cache<String, Serializable> cache;

    public CaffeineCache(com.github.benmanes.caffeine.cache.Cache<String, Serializable> cache) {
        this.cache = cache;
    }

    @Override
    public Serializable get(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public Map<String, Serializable> getAll(Collection<String> keys) {
        return cache.getAllPresent(keys);
    }

    @Override
    public boolean exists(String key) {
        return cache.getIfPresent(key) != null;
    }

    @Override
    public void put(String key, Serializable value) {
        cache.put(key, value);
    }

    @Override
    public Serializable putIfAbsent(String key, Serializable value) {
        Serializable old = get(key);
        if(old != null)
            cache.put(key, value);
        return old;
    }

    @Override
    public void putAll(Map<String, Serializable> elements) {
        cache.putAll(elements);
    }

    @Override
    public void update(String key, Serializable value) {
        putIfAbsent(key, value);
    }

    @Override
    public Collection<String> keys() {
        return cache.asMap().keySet();
    }

    @Override
    public void evict(String... keys) {
        cache.invalidateAll(Arrays.asList(keys));
    }

    @Override
    public void clear() {
        cache.cleanUp();
    }
}
