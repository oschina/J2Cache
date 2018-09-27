package net.oschina.j2cache.hibernate5.nonstop;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.entity.EntityPersister;

public class NonstopAwareEntityRegionAccessStrategy implements EntityRegionAccessStrategy {
    
    private final EntityRegionAccessStrategy actualStrategy;
    private final HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler;

    public NonstopAwareEntityRegionAccessStrategy(EntityRegionAccessStrategy actualStrategy, HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler) {
        this.actualStrategy = actualStrategy;
        this.hibernateNonstopExceptionHandler = hibernateNonstopExceptionHandler;
    }

    @Override
    public EntityRegion getRegion() {
        return this.actualStrategy.getRegion();
    }

    @Override
    public boolean afterInsert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
        try {
            return this.actualStrategy.afterInsert(session, key, value, version);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return false;
        }
    }

    @Override
    public boolean afterUpdate(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException {
        try {
            return this.actualStrategy.afterUpdate(session, key, value, currentVersion, previousVersion, lock);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return false;
        }
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
    public boolean insert(SharedSessionContractImplementor session, Object key, Object value, Object version) throws CacheException {
        try {
            return this.actualStrategy.insert(session, key, value, version);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return false;
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
    public boolean putFromLoad(SharedSessionContractImplementor session, Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride) throws CacheException {
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
    public boolean update(SharedSessionContractImplementor session, Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException {
        try {
            return this.actualStrategy.update(session, key, value, currentVersion, previousVersion);
        } catch (NonStopCacheException nonStopCacheException) {
            hibernateNonstopExceptionHandler.handleNonstopCacheException(nonStopCacheException);
            return false;
        }
    }

    @Override
    public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
        return DefaultCacheKeysFactory.staticCreateEntityKey( id, persister, factory, tenantIdentifier );
    }

    @Override
    public Object getCacheKeyId(Object cacheKey) {
        return DefaultCacheKeysFactory.staticGetEntityId(cacheKey);
    }
}