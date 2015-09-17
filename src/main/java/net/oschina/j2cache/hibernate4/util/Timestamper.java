package net.oschina.j2cache.hibernate4.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public final class Timestamper {

    public static final int BIN_DIGITS = Integer.getInteger("net.oschina.j2cache.hibernate4.redis.util.Timestamper.shift", 12);

    public static final int ONE_MS = 1 << BIN_DIGITS;

    private static final Logger LOG     = LoggerFactory.getLogger(Timestamper.class);
    private static final int    MAX_LOG = Integer.getInteger("net.oschina.j2cache.hibernate4.redis.util.Timestamper.log.max", 1) * 1000;

    private static final AtomicLong VALUE  = new AtomicLong();
    private static final AtomicLong LOGGED = new AtomicLong();


    private Timestamper() {
    }

    public static long next() {
        int runs = 0;
        while (true) {
            long base = SlewClock.timeMillis() << BIN_DIGITS;
            long maxValue = base + ONE_MS - 1;

            for (long current = VALUE.get(), update = Math.max(base, current + 1); update < maxValue;
                 current = VALUE.get(), update = Math.max(base, current + 1)) {
                if (VALUE.compareAndSet(current, update)) {
                    if (runs > 1) {
                        log(base, "Thread spin-waits on time to pass. Looped "
                                + "{} times, you might want to increase -Dnet.oschina.j2cache.hibernate4.redis.util.Timestamper.shift", runs);
                    }
                    return update;
                }
            }
            ++runs;
        }
    }

    private static void log(final long base, final String message, final Object... params) {
        if (LOG.isInfoEnabled()) {
            long thisLog = (base >> BIN_DIGITS) / MAX_LOG;
            long previousLog = LOGGED.get();
            if (previousLog != thisLog) {
                if (LOGGED.compareAndSet(previousLog, thisLog)) {
                    LOG.info(message, params);
                }
            }
        }
    }
}