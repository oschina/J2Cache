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
package net.oschina.j2cache;

import net.oschina.j2cache.util.SerializationUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 二级缓存接口
 * @author Winter Lau(javayou@gmail.com)
 */
public interface Level2Cache extends Cache {

    /**
     * 读取缓存数据字节数组
     * @param key
     * @return
     */
    byte[] getBytes(String key);

    /**
     * 同时读取多个 Key
     * @param keys
     * @return
     */
    List<byte[]> getBytes(Collection<String> keys);

    /**
     * 设置缓存数据字节数组
     * @param key
     * @param bytes
     */
    void setBytes(String key, byte[] bytes);

    /**
     * 同时设置多个数据
     * @param bytes
     */
    void setBytes(Map<String,byte[]> bytes);

    /**
     * 判断缓存数据是否存在
     * @param key cache key
     * @return
     */
    default boolean exists(String key) {
        return getBytes(key) != null;
    }

    /**
     * Return all keys
     *
     * @return 返回键的集合
     */
    Collection<String> keys() ;

    /**
     * Remove items from the cache
     *
     * @param keys Cache key
     */
    void evict(String...keys);

    /**
     * Clear the cache
     */
    void clear();

    @Override
    default Object get(String key) {
        byte[] bytes = getBytes(key);
        try {
            return SerializationUtils.deserialize(bytes);
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

    @Override
    default Map<String, Object> get(Collection<String> keys) {
        Map<String, Object> results = new HashMap<>();
        if(keys.size() > 0) {
            List<byte[]> bytes = getBytes(keys);
            int i = 0;
            try {
                for (String key : keys)
                    results.put(key, SerializationUtils.deserialize(bytes.get(i++)));
            } catch (IOException e) {
                throw new CacheException(e);
            }
        }
        return results;
    }

    @Override
    default void put(String key, Object value) {
        try {
            setBytes(key, SerializationUtils.serialize(value));
        } catch (IOException e) {
            throw new CacheException(e);
        }
    }

    @Override
    default void put(Map<String, Object> elements) {
        if(elements.size() > 0)
            setBytes(elements.entrySet().stream().collect(Collectors.toMap(p -> p.getKey(), p->SerializationUtils.serializeWithoutException(p.getValue()))));
    }

}
