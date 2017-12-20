package net.oschina.j2cache.ehcache;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheExpiredListener;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * EHCache
 */
class EhCache implements Cache, CacheEventListener {
	
	private net.sf.ehcache.Cache cache;
	private CacheExpiredListener listener;

	/**
	 * Creates a new EhCache instance
	 *
	 * @param cache The underlying EhCache instance to use.
	 * @param listener cache listener
	 */
	public EhCache(net.sf.ehcache.Cache cache, CacheExpiredListener listener) {
		this.cache = cache;
		this.cache.getCacheEventNotificationService().registerListener(this);
		this.listener = listener;
	}

	@Override
	public Set<Serializable> keys() throws CacheException {
		Set<Serializable> keys = new HashSet<>();
		keys.addAll(this.cache.getKeys());
		return keys;
	}

	/**
	 * Gets a value of an element which matches the given key.
	 *
	 * @param key the key of the element to return.
	 * @return The value placed into the cache with an earlier put, or null if not found or expired
	 * @throws CacheException cache exception
	 */
	@Override
	public Serializable get(Serializable key) throws CacheException {
		try {
			if ( key == null ) 
				return null;
			Element element = cache.get( key );
			if ( element != null )
				return (Serializable)element.getObjectValue();

			return null;
		}
		catch (net.sf.ehcache.CacheException e) {
			throw new CacheException( e );
		}
	}

	/**
	 * Puts an object into the cache.
	 *
	 * @param key   a key
	 * @param value a value
	 * @throws CacheException if the {@link CacheManager}
	 *                        is shutdown or another {@link Exception} occurs.
	 */
	@Override
	public void update(Serializable key, Serializable value) throws IOException {
		put( key, value );
	}

	/**
	 * Puts an object into the cache.
	 *
	 * @param key   a key
	 * @param value a value
	 * @throws CacheException if the {@link CacheManager}
	 *                        is shutdown or another {@link Exception} occurs.
	 */
	@Override
	public void put(Serializable key, Serializable value) throws CacheException {
		Element element = new Element( key, value );
		cache.put( element );
	}

	/**
	 * Removes the element which matches the key
	 * If no element matches, nothing is removed and no Exception is thrown.
	 *
	 * @param key the key of the element to remove
	 * @throws CacheException cache exception
	 */
	@Override
	public void evict(Serializable key) throws CacheException {
		try {
			cache.remove( key );
		} catch (IllegalStateException | net.sf.ehcache.CacheException e) {
			throw new CacheException( e );
		}
	}

	/* (non-Javadoc)
	 * @see net.oschina.j2cache.Cache#batchRemove(java.util.List)
	 */
	@Override
	public void evicts(List<Serializable> keys) throws CacheException {
		cache.removeAll(keys);
	}

	/**
	 * Remove all elements in the cache, but leave the cache
	 * in a useable state.
	 *
	 * @throws CacheException cache exception
	 */
	public void clear() throws CacheException {
		cache.removeAll();
	}

	public Object clone() throws CloneNotSupportedException { 
		throw new CloneNotSupportedException(); 
	}

	@Override
	public void dispose() {}

	@Override
	public void notifyElementEvicted(Ehcache cache, Element elem) {
		if(listener != null){
			listener.notifyElementExpired(cache.getName(), (Serializable)elem.getObjectKey());
		}
	}

	@Override
	public void notifyElementExpired(Ehcache cache, Element elem) {
		if(listener != null){
			listener.notifyElementExpired(cache.getName(), (Serializable)elem.getObjectKey());
		}
	}

	@Override
	public void notifyElementPut(Ehcache cache, Element elem) throws net.sf.ehcache.CacheException {}

	@Override
	public void notifyElementRemoved(Ehcache cache, Element elem) throws net.sf.ehcache.CacheException {}

	@Override
	public void notifyElementUpdated(Ehcache cache, Element elem) throws net.sf.ehcache.CacheException {}

	@Override
	public void notifyRemoveAll(Ehcache cache) {}

}