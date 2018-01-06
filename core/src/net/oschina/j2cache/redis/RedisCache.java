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
package net.oschina.j2cache.redis;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.util.SerializationUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Redis 的扩展接口
 * @author Winter Lau(javayou@gmail.com)
 */
public interface RedisCache extends Cache {

    @Override
    @Deprecated
    default Serializable get(String key) throws IOException {
        return getObject(key);
    }

    @Override
    @Deprecated
    default Map getAll(Collection<String> keys) throws IOException {
        return getObjects(keys);
    }

    Long incr(String key, long l) ;

    Long decr(String key, long l) ;

    byte[] getBytes(String key);

    default String getString(String key) {
        byte[] bytes = getBytes(key);
        return (bytes != null) ? new String(bytes) : null;
    }

    default Long getLong(String key) {
        String value = getString(key);
        return (value != null) ? Long.parseLong(value) : null;
    }

    default Serializable getObject(String key) throws IOException {
        byte[] bytes = getBytes(key);
        return (bytes != null) ? SerializationUtils.deserialize(bytes) : null;
    }

    default Map<String, Long> getLong(Collection<String> keys){
        return keys.stream().collect(Collectors.toMap(Function.identity(), key -> getLong(key)));
    }

    default Map<String, String> getString(Collection<String> keys) {
        return keys.stream().collect(Collectors.toMap(Function.identity(), key -> getString(key)));
    }

    default Map<String, byte[]> getBytes(Collection<String> keys) {
        return keys.stream().collect(Collectors.toMap(Function.identity(), key -> getBytes(key)));
    }

    default Map<String, Serializable> getObjects(Collection<String> keys) throws IOException {
        Map<String, Serializable> values = new HashMap<>();
        for(String key : keys) {
            values.put(key, getObject(key));
        }
        return values;
    }

}
