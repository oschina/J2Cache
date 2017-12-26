package net.oschina.j2cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

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
	Serializable get(Serializable key) throws IOException;
	
	/**
	 * Add an item to the cache, nontransactionally, with
	 * failfast semantics
	 *
	 * @param key cache key
	 * @param value cache value
	 * @throws IOException io exception
	 */
	void put(Serializable key, Serializable value) throws IOException;

	/**
	 * Add an item to the cache
	 *
	 * @param key cache key
	 * @param value cache value
	 * @throws IOException io exception
	 */
	void update(Serializable key, Serializable value) throws IOException;

	/**
	 * Return all keys
	 *
	 * @return 返回键的集合
	 * @throws IOException io exception
	 */
	Set<Serializable> keys() throws IOException ;
	
	/**
	 * Remove an item from the cache
	 *
	 * @param key Cache key
	 * @throws IOException io exception
	 */
	void evict(Serializable key) throws IOException;

	/**
	 * Batch remove cache objects
	 *
	 * @param keys the cache keys to be evicted
	 * @throws IOException io exception
	 */
	void evicts(List<Serializable> keys) throws IOException;
	
	/**
	 * Clear the cache
	 *
	 * @throws IOException io exception
	 */
	void clear() throws IOException;

}
