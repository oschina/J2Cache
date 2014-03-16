package net.oschina.j2cache;

import java.util.List;

/**
 * Implementors define a caching algorithm. All implementors
 * <b>must</b> be threadsafe.
 * @author liudong
 */
public interface Cache {

	/**
	 * Get an item from the cache, nontransactionally
	 * @param key
	 * @return the cached object or <tt>null</tt>
	 * @throws CacheException
	 */
	public Object get(Object key) throws CacheException;
	
	/**
	 * Add an item to the cache, nontransactionally, with
	 * failfast semantics
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
	public void put(Object key, Object value) throws CacheException;
	
	/**
	 * Add an item to the cache
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
	public void update(Object key, Object value) throws CacheException;

	@SuppressWarnings("rawtypes")
	public List keys() throws CacheException ;
	
	/**
	 * Remove an item from the cache
	 */
	public void remove(Object key) throws CacheException;
	
	/**
	 * Batch remove cache objects
	 * @param keys
	 * @throws CacheException
	 */
	public void batchRemove(List<Object> keys) throws CacheException;
	
	/**
	 * Clear the cache
	 */
	public void clear() throws CacheException;
	
	/**
	 * Clean up
	 */
	public void destroy() throws CacheException;
	
}
