package net.oschina.j2cache.hibernate5.util.lang;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class VicariousThreadLocal<T> extends ThreadLocal<T> {

    private static final ThreadLocal<WeakReference<Thread>> weakThread = new ThreadLocal();
    private static final Object UNINITIALISED = new Object();
    private final ThreadLocal<WeakReference<VicariousThreadLocal.Holder>> local = new ThreadLocal();
    private volatile VicariousThreadLocal.Holder strongRefs;
    private static final AtomicReferenceFieldUpdater<VicariousThreadLocal, VicariousThreadLocal.Holder> strongRefsUpdater = AtomicReferenceFieldUpdater.newUpdater(VicariousThreadLocal.class, VicariousThreadLocal.Holder.class, "strongRefs");
    private final ReferenceQueue<Object> queue = new ReferenceQueue();

    static WeakReference<Thread> currentThreadRef() {
        WeakReference<Thread> ref = (WeakReference)weakThread.get();
        if (ref == null) {
            ref = new WeakReference(Thread.currentThread());
            weakThread.set(ref);
        }

        return ref;
    }

    public VicariousThreadLocal() {
    }

    public T get() {
        WeakReference<VicariousThreadLocal.Holder> ref = (WeakReference)this.local.get();
        VicariousThreadLocal.Holder holder;
        Object value;
        if (ref != null) {
            holder = (VicariousThreadLocal.Holder)ref.get();
            value = holder.value;
            if (value != UNINITIALISED) {
                return (T) value;
            }
        } else {
            holder = this.createHolder();
        }

        value = this.initialValue();
        holder.value = value;
        return (T) value;
    }

    public void set(T value) {
        WeakReference<VicariousThreadLocal.Holder> ref = (WeakReference)this.local.get();
        VicariousThreadLocal.Holder holder = ref != null ? (VicariousThreadLocal.Holder)ref.get() : this.createHolder();
        holder.value = value;
    }

    private VicariousThreadLocal.Holder createHolder() {
        this.poll();
        VicariousThreadLocal.Holder holder = new VicariousThreadLocal.Holder(this.queue);
        WeakReference ref = new WeakReference(holder);

        VicariousThreadLocal.Holder old;
        do {
            old = this.strongRefs;
            holder.next = old;
        } while(!strongRefsUpdater.compareAndSet(this, old, holder));

        this.local.set(ref);
        return holder;
    }

    public void remove() {
        WeakReference<VicariousThreadLocal.Holder> ref = (WeakReference)this.local.get();
        if (ref != null) {
            ((VicariousThreadLocal.Holder)ref.get()).value = UNINITIALISED;
        }

    }

    public void poll() {
        ReferenceQueue var1 = this.queue;
        synchronized(this.queue) {
            if (this.queue.poll() != null) {
                while(this.queue.poll() != null) {
                    ;
                }

                VicariousThreadLocal.Holder first = this.strongRefs;
                if (first != null) {
                    VicariousThreadLocal.Holder link = first;
                    VicariousThreadLocal.Holder next = first.next;

                    while(next != null) {
                        if (next.get() == null) {
                            next = next.next;
                            link.next = next;
                        } else {
                            link = next;
                            next = next.next;
                        }
                    }

                    if (first.get() == null && !strongRefsUpdater.weakCompareAndSet(this, first, first.next)) {
                        first.value = null;
                    }

                }
            }
        }
    }

    private static class Holder extends WeakReference<Object> {
        VicariousThreadLocal.Holder next;
        Object value;

        Holder(ReferenceQueue<Object> queue) {
            super(VicariousThreadLocal.currentThreadRef(), queue);
            this.value = VicariousThreadLocal.UNINITIALISED;
        }
    }
}
