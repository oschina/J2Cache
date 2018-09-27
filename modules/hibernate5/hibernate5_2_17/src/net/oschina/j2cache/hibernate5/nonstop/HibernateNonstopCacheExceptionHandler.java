package net.oschina.j2cache.hibernate5.nonstop;

import net.oschina.j2cache.hibernate5.log.J2CacheMessageLogger;
import org.jboss.logging.Logger;

public final class HibernateNonstopCacheExceptionHandler {

    public static final String HIBERNATE_THROW_EXCEPTION_ON_TIMEOUT_PROPERTY = "J2Cache.hibernate.propagateNonStopCacheException";
    public static final String HIBERNATE_LOG_EXCEPTION_STACK_TRACE_PROPERTY = "J2Cache.hibernate.logNonStopCacheExceptionStackTrace";

    private static final J2CacheMessageLogger LOG = Logger.getMessageLogger(J2CacheMessageLogger.class, HibernateNonstopCacheExceptionHandler.class.getName());
    private static final HibernateNonstopCacheExceptionHandler INSTANCE = new HibernateNonstopCacheExceptionHandler();

    private HibernateNonstopCacheExceptionHandler() {
    }

    public static HibernateNonstopCacheExceptionHandler getInstance() {
        return INSTANCE;
    }

    public void handleNonstopCacheException(NonStopCacheException nonStopCacheException) {
        if (Boolean.getBoolean(HIBERNATE_THROW_EXCEPTION_ON_TIMEOUT_PROPERTY)) {
            throw nonStopCacheException;
        } else {
            if (Boolean.getBoolean(HIBERNATE_LOG_EXCEPTION_STACK_TRACE_PROPERTY)) {
                LOG.debug("Ignoring NonstopCacheException - " + nonStopCacheException.getMessage(), nonStopCacheException);
            } else {
                LOG.debug("Ignoring NonstopCacheException - " + nonStopCacheException.getMessage());
            }
        }
    }
}