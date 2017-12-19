package net.oschina.j2cache;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 空的缓存Provider
 * @author winterlau
 */
public class NullCache<K,V> implements Cache<K,V> {
	@Override
	public V get(K key) throws IOException, CacheException {
		return null;
	}

	@Override
	public void put(K key, V value) throws IOException, CacheException {

	}

	@Override
	public void update(K key, V value) throws IOException, CacheException {

	}

	@Override
	public Set<K> keys() throws IOException, CacheException {
		return null;
	}

	@Override
	public void evict(K key) throws IOException, CacheException {

	}

	@Override
	public void evicts(List<K> keys) throws IOException, CacheException {

	}

	@Override
	public void clear() throws IOException, CacheException {

	}

	@Override
	public void destroy() throws IOException, CacheException {

	}
}
