package net.oschina.j2cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * 空的缓存Provider
 * @author Winter Lau(javayou@gmail.com)
 */
public class NullCache implements Cache {
	@Override
	public Serializable get(Serializable key) throws IOException, CacheException {
		return null;
	}

	@Override
	public void put(Serializable key, Serializable value) throws IOException, CacheException {

	}

	@Override
	public void update(Serializable key, Serializable value) throws IOException, CacheException {

	}

	@Override
	public Set<Serializable> keys() throws IOException, CacheException {
		return null;
	}

	@Override
	public void evict(Serializable key) throws IOException, CacheException {

	}

	@Override
	public void evicts(List<Serializable> keys) throws IOException, CacheException {

	}

	@Override
	public void clear() throws IOException, CacheException {

	}
}
