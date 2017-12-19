package net.oschina.j2cache;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Implementors define a caching algorithm. All implementors
 * <b>must</b> be threadsafe.
 * @author Winter Lau
 */
public interface Cache<K, V> {

	/**
	 * Get an item from the cache, nontransactionally
	 * @param key cache key
	 * @return the cached object or null
	 */
	V get(K key) throws IOException, CacheException;
	
	/**
	 * Add an item to the cache, nontransactionally, with
	 * failfast semantics
	 * @param key cache key
	 * @param value cache value
	 */
	void put(K key, V value) throws IOException, CacheException;

	/**
	 * Add an item to the cache
	 * @param key cache key
	 * @param value cache value
	 */
	void update(K key, V value) throws IOException, CacheException;

	/**
	 * Return all keys
	 * @return
	 * @throws IOException
	 * @throws CacheException
	 */
	Set<K> keys() throws IOException, CacheException ;
	
	/**
	 * @param key Cache key
	 * Remove an item from the cache
	 */
	void evict(K key) throws IOException, CacheException;
	
	/**
	 * Batch remove cache objects
	 * @param keys the cache keys to be evicted
	 */
	void evicts(List<K> keys) throws IOException, CacheException;
	
	/**
	 * Clear the cache
	 */
	void clear() throws IOException, CacheException;
	
	/**
	 * Clean up
	 */
	void destroy() throws IOException, CacheException;
	
}
