package net.oschina.j2cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Implementors define a caching algorithm. All implementors
 * <b>must</b> be threadsafe.
 * @author Winter Lau
 */
public interface Cache {

	/**
	 * Get an item from the cache, nontransactionally
	 * @param key cache key
	 * @return the cached object or null
	 */
	Serializable get(Serializable key) throws IOException;
	
	/**
	 * Add an item to the cache, nontransactionally, with
	 * failfast semantics
	 * @param key cache key
	 * @param value cache value
	 */
	void put(Serializable key, Serializable value) throws IOException;

	/**
	 * Add an item to the cache
	 * @param key cache key
	 * @param value cache value
	 */
	void update(Serializable key, Serializable value) throws IOException;

	/**
	 * Return all keys
	 * @return
	 * @throws IOException
	 * @throws CacheException
	 */
	Set<Serializable> keys() throws IOException ;
	
	/**
	 * @param key Cache key
	 * Remove an item from the cache
	 */
	void evict(Serializable key) throws IOException;

	/**
	 * Batch remove cache objects
	 * @param keys the cache keys to be evicted
	 */
	void evicts(List<Serializable> keys) throws IOException;
	
	/**
	 * Clear the cache
	 */
	void clear() throws IOException;

}
