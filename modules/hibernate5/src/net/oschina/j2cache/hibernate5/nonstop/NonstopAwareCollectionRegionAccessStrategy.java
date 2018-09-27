package net.oschina.j2cache.hibernate5.nonstop;


import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;

public class NonstopAwareCollectionRegionAccessStrategy implements CollectionRegionAccessStrategy {
    
    private final CollectionRegionAccessStrategy actualStrategy;
    private final HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler;

    public NonstopAwareCollectionRegionAccessStrategy(CollectionRegionAccessStrategy actualStrategy, HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler) {
        this.actualStrategy = actualStrategy;
        this.hibernateNonstopExceptionHandler = hibernateNonstopExceptionHandler;
    }

    @Override
    public CollectionRegion getRegion() {
        return this.actualStrategy.getRegion();
    }

    @Override
    public void evict(Object key) throws CacheException {
        try {
            this.actualStrategy.evict(key);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
        }
    }

    @Override
    public void evictAll() throws CacheException {
        try {
            this.actualStrategy.evictAll();
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
        }
    }

    @Override
    public Object get(SharedSessionContractImplementor session, Object key, long txTimestamp) throws CacheException {
        try {
            return this.actualStrategy.get(session, key, txTimestamp);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return null;
        }
    }

    @Override
    public SoftLock lockItem(SharedSessionContractImplementor session, Object key, Object version) throws CacheException {
        try {
            return this.actualStrategy.lockItem(session, key, version);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return null;
        }
    }

    @Override
    public SoftLock lockRegion() throws CacheException {
        try {
            return this.actualStrategy.lockRegion();
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return null;
        }
    }

    @Override
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
            throws CacheException {
        try {
            return this.actualStrategy.putFromLoad(session, key, value, txTimestamp, version, minimalPutOverride);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return false;
        }
    }

    @Override
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version) throws CacheException {
        try {
            return this.actualStrategy.putFromLoad(session, key, value, txTimestamp, version);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return false;
        }
    }

    @Override
    public void remove(SharedSessionContractImplementor session, Object key) throws CacheException {
        try {
            this.actualStrategy.remove(session, key);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
        }
    }

    @Override
    public void removeAll() throws CacheException {
        try {
            this.actualStrategy.removeAll();
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
        }
    }

    @Override
    public void unlockItem(SharedSessionContractImplementor session, Object key, SoftLock lock) throws CacheException {
        try {
            this.actualStrategy.unlockItem(session, key, lock);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
        }
    }

    @Override
    public void unlockRegion(SoftLock lock) throws CacheException {
        try {
            this.actualStrategy.unlockRegion(lock);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
        }
    }

    @Override
    public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.staticCreateCollectionKey( id, persister, factory, tenantIdentifier );
    }

    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetCollectionId(cacheKey);
    }
}