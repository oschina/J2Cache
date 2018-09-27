package net.oschina.j2cache.hibernate5.util;

import java.util.concurrent.atomic.AtomicLong;

import net.oschina.j2cache.hibernate5.util.TimeProviderLoader;
import net.oschina.j2cache.hibernate5.util.lang.VicariousThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class SlewClock {

    private static final Logger LOG = LoggerFactory.getLogger(net.oschina.j2cache.hibernate5.util.SlewClock.class);
    private static final net.oschina.j2cache.hibernate5.util.SlewClock.TimeProvider PROVIDER = TimeProviderLoader.getTimeProvider();
    private static final long DRIFT_MAXIMAL = (long)Integer.getInteger("net.oschina.j2cache.hibernate5.util.Timestamper.drift.max", 50);
    private static final long SLEEP_MAXIMAL = (long)Integer.getInteger("net.oschina.j2cache.hibernate5.util.Timestamper.sleep.max", 50);
    private static final int SLEEP_BASE = Integer.getInteger("net.oschina.j2cache.hibernate5.util.Timestamper.sleep.min", 25);
    private static final AtomicLong CURRENT = new AtomicLong(-9223372036854775808L);
    private static final VicariousThreadLocal<Long> OFFSET = new VicariousThreadLocal();

    private SlewClock() {
    }

    /** @deprecated */
    @Deprecated
    static void realignWithTimeProvider() {
        CURRENT.set(getCurrentTime());
    }

    static long timeMillis() {
        boolean interrupted = false;

        try {
            while(true) {
                long mono = CURRENT.get();
                long wall = getCurrentTime();
                long delta;
                if (wall == mono) {
                    OFFSET.remove();
                    delta = wall;
                    return delta;
                }

                if (wall > mono) {
                    if (CURRENT.compareAndSet(mono, wall)) {
                        OFFSET.remove();
                        delta = wall;
                        return delta;
                    }
                } else {
                    delta = mono - wall;
                    if (delta < DRIFT_MAXIMAL) {
                        OFFSET.remove();
                        long var15 = mono;
                        return var15;
                    }

                    Long lastDelta = (Long)OFFSET.get();
                    long sleep;
                    if (lastDelta != null && delta >= lastDelta) {
                        OFFSET.set(Math.max(delta, lastDelta));

                        try {
                            sleep = sleepTime(delta, lastDelta);
                            LOG.trace("{} sleeping for {}ms to adjust for wall-clock drift.", Thread.currentThread(), sleep);
                            Thread.sleep(sleep);
                        } catch (InterruptedException var13) {
                            interrupted = true;
                        }
                    } else if (CURRENT.compareAndSet(mono, mono + 1L)) {
                        OFFSET.set(delta);
                        sleep = mono + 1L;
                        return sleep;
                    }
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }

        }
    }

    static boolean isThreadCatchingUp() {
        return OFFSET.get() != null;
    }

    static long behind() {
        Long offset = (Long)OFFSET.get();
        return offset == null ? 0L : offset;
    }

    private static long sleepTime(long current, long previous) {
        long target = (long)SLEEP_BASE + (current - previous) * 2L;
        return Math.min(target > 0L ? target : (long)SLEEP_BASE, SLEEP_MAXIMAL);
    }

    private static long getCurrentTime() {
        return PROVIDER.currentTimeMillis();
    }

    interface TimeProvider {
        long currentTimeMillis();
    }
}
