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

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * 缓存支持对单个元素设置有效期的接口(目前只有 Ehcache 2.x 支持此接口）
 * @author Winter Lau (javayou@gmail.com)
 */
public interface TTLEnableCache extends Cache {

    /**
     * Add an item to cache with TTL setting
     * @param key
     * @param value
     * @param timeToLiveInSeconds
     * @throws IOException
     */
    void put(String key, Serializable value, int timeToLiveInSeconds) throws IOException ;

    /**
     * Put an element in the cache with TTL setting if no element is currently mapped to the elements key.
     * @param key
     * @param value
     * @param timeToLiveInSeconds
     * @return
     * @throws IOException
     */
    Serializable putIfAbsent(String key, Serializable value, int timeToLiveInSeconds) throws IOException ;

    /**
     * 批量插入数据，带 TTL 设置
     * @param elements
     * @param timeToLiveInSeconds
     * @throws IOException
     */
    void putAll(Map<String, Serializable> elements, int timeToLiveInSeconds) throws IOException;

}
