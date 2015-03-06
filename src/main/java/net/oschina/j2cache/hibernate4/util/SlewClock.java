package net.oschina.j2cache.hibernate4.util;

import net.sf.ehcache.util.lang.VicariousThreadLocal;

import java.util.concurrent.atomic.AtomicLong;

final class SlewClock {

//    private static final TimeProvider PROVIDER = TimeProviderLoader.getTimeProvider();

    private static final long DRIFT_MAXIMAL = Integer.getInteger("net.sf.ehcache.util.Timestamper.drift.max", 50);

    private static final long SLEEP_MAXIMAL = Integer.getInteger("net.sf.ehcache.util.Timestamper.sleep.max", 50);

    private static final int  SLEEP_BASE    = Integer.getInteger("net.sf.ehcache.util.Timestamper.sleep.min", 25);

    private static final AtomicLong CURRENT = new AtomicLong(getCurrentTime());

    private static final VicariousThreadLocal<Long> OFFSET = new VicariousThreadLocal<Long>();

    private SlewClock() {
        // You shall not instantiate me!
    }

    /**
     * Will return the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
     * But without ever going back. If a movement back in time is detected, the method will slew until time caught up
     * @return The difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
     */
    static long timeMillis() {
        boolean interrupted = false;
        try {
            while (true) {
                long mono = CURRENT.get();
                long wall = getCurrentTime();
                if (wall == mono) {
                    OFFSET.remove();
                    return wall;
                } else if (wall >= mono) {
                    if (CURRENT.compareAndSet(mono, wall)) {
                        OFFSET.remove();
                        return wall;
                    }
                } else {
                    long delta = mono - wall;
                    if (delta < DRIFT_MAXIMAL) {
                        OFFSET.remove();
                        return mono;
                    } else {
                        Long lastDelta = OFFSET.get();
                        if (lastDelta == null || delta < lastDelta) {
                            long update = wall - delta;
                            update = update < mono ? mono + 1 : update;
                            if (CURRENT.compareAndSet(mono, update)) {
                                OFFSET.set(Long.valueOf(delta));
                                return update;
                            }
                        } else {
                            try {
                                Thread.sleep(sleepTime(delta, lastDelta));
                            } catch (InterruptedException e) {
                                interrupted = true;
                            }
                        }
                    }
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Verifies whether the current thread is currently catching up on time.
     * To be meaning full, this method has to be called after the thread has called {@link #timeMillis()} at least once
     * @return true if the thread is being marked as catching up on time
     */
    static boolean isThreadCatchingUp() {
        return OFFSET.get() != null;
    }

    /**
     * The method will check how much behind is the current thread compared to the wall clock since the last {@link #timeMillis()} call.
     * To be meaning full, this method has to be called after the thread has called {@link #timeMillis()} at least once
     * @return the amount of milliseconds the thread is behind the wall clock, 0 if none.
     */
    static long behind() {
        Long offset = OFFSET.get();
        return offset == null ? 0 : offset;
    }

    private static long sleepTime(final long current, final long previous) {
        long target = SLEEP_BASE + (current - previous) * 2;
        return Math.min(target > 0 ? target : SLEEP_BASE, SLEEP_MAXIMAL);
    }

    private static long getCurrentTime() {
        return System.currentTimeMillis();/*PROVIDER.currentTimeMillis()*/
    }

    /**
     * Defines how the {@link SlewClock} utility class will get to the wall clock
     */
    interface TimeProvider {

        /**
         * @return the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
         */
        long currentTimeMillis();

    }
}
