package net.oschina.j2cache.hibernate5.util;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Timestamper {

    public static final int BIN_DIGITS = Integer.getInteger("net.oschina.j2cache.hibernate5.util.shift", 12);
    public static final int ONE_MS;
    private static final Logger LOG;
    private static final int MAX_LOG;
    private static final AtomicLong VALUE;
    private static final AtomicLong LOGGED;

    private Timestamper() {
    }

    public static long next() {
        int runs = 0;

        while(true) {
            long base = SlewClock.timeMillis() << BIN_DIGITS;
            long maxValue = base + (long)ONE_MS - 1L;
            long current = VALUE.get();

            for(long update = Math.max(base, current + 1L); update < maxValue; update = Math.max(base, current + 1L)) {
                if (VALUE.compareAndSet(current, update)) {
                    if (runs > 1) {
                        log(base, "Thread spin-waits on time to pass. Looped {} times, you might want to increase -Dnet.oschina.j2cache.hibernate5.util.shift", runs);
                    }

                    return update;
                }

                current = VALUE.get();
            }

            ++runs;
        }
    }

    private static void log(long base, String message, Object... params) {
        if (LOG.isInfoEnabled()) {
            long thisLog = (base >> BIN_DIGITS) / (long)MAX_LOG;
            long previousLog = LOGGED.get();
            if (previousLog != thisLog && LOGGED.compareAndSet(previousLog, thisLog)) {
                LOG.info(message, params);
            }
        }

    }

    static {
        ONE_MS = 1 << BIN_DIGITS;
        LOG = LoggerFactory.getLogger(Timestamper.class);
        MAX_LOG = Integer.getInteger("net.oschina.j2cache.hibernate5.util.log.max", 1) * 1000;
        VALUE = new AtomicLong();
        LOGGED = new AtomicLong();
    }
}
