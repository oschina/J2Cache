package net.oschina.j2cache.hibernate5.util;

import net.oschina.j2cache.hibernate5.util.SlewClock.TimeProvider;

final class TimeProviderLoader {

    private static TimeProvider timeProvider = new TimeProvider() {
        public final long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    };

    private TimeProviderLoader() {
    }

    public static synchronized TimeProvider getTimeProvider() {
        return timeProvider;
    }

    public static synchronized void setTimeProvider(TimeProvider timeProvider) {
        timeProvider = timeProvider;
    }
}
