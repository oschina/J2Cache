package net.oschina.j2cache.hibernate5.nonstop;

import org.hibernate.cache.CacheException;

public class NonStopCacheException extends CacheException {

    public NonStopCacheException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NonStopCacheException(final String message) {
        super(message);
    }

    public NonStopCacheException(final Throwable cause) {
        super(cause);
    }
}
