/**
 * 
 */
package net.oschina.j2cache.hibernate3;

import net.oschina.j2cache.CacheChannel;
import net.oschina.j2cache.hibernate4.util.Timestamper;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * J2Cache implements Hibernate Cache Region
 * @author winterlau
 */
public class J2CacheRegion implements GeneralDataRegion {
	
	private String regionName;
	private CacheChannel cache;
	
	public J2CacheRegion(String name, CacheChannel cache){
		this.regionName = name;
		this.cache = cache;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.cache.Region#destroy()
	 */
	@Override
	public void destroy() throws CacheException {
	}

    @Override
    public boolean contains(Object key) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.hibernate.cache.Region#getElementCountInMemory()
     */
	@Override
	public long getElementCountInMemory() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.cache.Region#getElementCountOnDisk()
	 */
	@Override
	public long getElementCountOnDisk() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.cache.Region#getName()
	 */
	@Override
	public String getName() {
		return regionName;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.cache.Region#getSizeInMemory()
	 */
	@Override
	public long getSizeInMemory() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.cache.Region#getTimeout()
	 */
	@Override
	public int getTimeout() {
		return -1;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.cache.Region#nextTimestamp()
	 */
	@Override
	public long nextTimestamp() {
		return Timestamper.next();
	}

	/* (non-Javadoc)
	 * @see org.hibernate.cache.Region#toMap()
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Map toMap() {
        try {
            Map<Object, Object> result = new HashMap<Object, Object>();
            for (Object key : cache.keys(this.regionName)) {
                Object e = cache.get(this.regionName, key);
                if (e != null) {
                    result.put(key, e);
                }
            }
            return result;
        } catch (Exception e) {
            throw new CacheException(e);
        }
	}

	@Override
	public void evict(Object key) throws CacheException {
		cache.evict(this.regionName, key);
	}

	@Override
	public void evictAll() throws CacheException {
		cache.clear(this.regionName);
	}

	@Override
	public Object get(Object key) throws CacheException {
		return cache.get(this.regionName, key);
	}

	@Override
	public void put(Object key, Object value) throws CacheException {
		cache.set(this.regionName, key, value);
	}

	private static class Transactional extends J2CacheRegion implements TransactionalDataRegion {

		public Transactional(String name, CacheChannel cache) {
			super(name, cache);
		}

		@Override
		public CacheDataDescription getCacheDataDescription() {
			return null;
		}

		@Override
		public boolean isTransactionAware() {
			return false;
		}
		
	}
	
	final static class QueryResults extends J2CacheRegion implements QueryResultsRegion {
		public QueryResults(String name, CacheChannel cache) {
			super(name, cache);
		}
	}
	
	final static class Entity extends Transactional implements EntityRegion {

		public Entity(String name, CacheChannel cache) {
			super(name, cache);
		}

		@Override
		public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
			return null;
		}
		
	}
	
	final static class Collection extends Transactional implements CollectionRegion {

		public Collection(String name, CacheChannel cache) {
			super(name, cache);
			// TODO Auto-generated constructor stub
		}

		@Override
		public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
			return null;
		}
		
	}
	
	final static class Timestamps extends J2CacheRegion implements TimestampsRegion {

		public Timestamps(String name, CacheChannel cache) {
			super(name, cache);
		}
		
	}
	
}
