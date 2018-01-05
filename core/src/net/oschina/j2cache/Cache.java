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
import java.util.Collection;
import java.util.Map;

/**
 * Cache Data Operation Interface
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public interface Cache {

	/**
	 * Get an item from the cache, nontransactionally
	 * 
	 * @param key cache key
	 * @return the cached object or null
	 * @throws IOException io exception
	 */
	Serializable get(String key) throws IOException;

	/**
	 * 批量获取缓存对象
	 * @param keys cache keys
	 * @return return key-value objects
	 * @throws IOException io exception
	 */
	Map<String, Serializable> getAll(Collection<String> keys) throws IOException;

	/**
	 * 判断缓存是否存在
	 * @param key cache key
	 * @return true if key exists
	 * @throws IOException io exception
	 */
	boolean exists(String key) throws IOException;
	
	/**
	 * Add an item to the cache, nontransactionally, with
	 * failfast semantics
	 *
	 * @param key cache key
	 * @param value cache value
	 * @throws IOException io exception
	 */
	void put(String key, Serializable value) throws IOException;

	/**
	 * Put an element in the cache if no element is currently mapped to the elements key.
	 * @param key cache key
	 * @param value cache object
	 * @return return old element exists in cache
	 * @throws IOException io exception
	 */
	Serializable putIfAbsent(String key, Serializable value) throws IOException ;

	/**
	 * 批量插入数据
	 * @param elements objects to be put in cache
	 * @throws IOException io exception
	 */
	void putAll(Map<String, Serializable> elements) throws IOException;

	/**
	 * Return all keys
	 *
	 * @return 返回键的集合
	 * @throws IOException io exception
	 */
	Collection<String> keys() throws IOException ;
	
	/**
	 * Remove an item from the cache
	 *
	 * @param keys Cache key
	 * @throws IOException io exception
	 */
	void evict(String...keys) throws IOException;

	/**
	 * Clear the cache
	 *
	 * @throws IOException io exception
	 */
	void clear() throws IOException;

}
