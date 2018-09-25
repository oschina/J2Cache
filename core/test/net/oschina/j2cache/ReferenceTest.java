package net.oschina.j2cache;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Java 引用测试
 */
public class ReferenceTest {

    private static ReferenceQueue<VeryBig> rq = new ReferenceQueue<>();

    public static void checkQueue() {
        Reference<? extends VeryBig> ref = null;
        while ((ref = rq.poll()) != null) {
            System.out.println("In queue: "    + ((VeryBigWeakReference) (ref)).id);
        }
    }

    public static void main2(String args[]) {
        int size = 3;
        LinkedList<WeakReference<VeryBig>> weakList = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            weakList.add(new VeryBigWeakReference(new VeryBig("Weak " + i), rq));
            System.out.println("Just created weak: " + weakList.getLast());

        }

        System.gc();
        try { // 下面休息几分钟，让上面的垃圾回收线程运行完成
            Thread.currentThread().sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        checkQueue();
    }

    public static void main1(String[] args) {
        WeakReference<String> sr = new WeakReference<>(new String("hello"));
        System.out.println(sr.get());
        System.gc();                //通知JVM的gc进行垃圾回收
        System.out.println(sr.get());
    }


    public static void main(String[] args) {
        ReferenceQueue<String> queue = new ReferenceQueue<>();
        PhantomReference<String> pr = new PhantomReference<>(new String("hello"), queue);
        System.out.println(pr.get());
    }
}

class VeryBig {
    public String id;
    // 占用空间,让线程进行回收
    byte[] b = new byte[2 * 1024];

    public VeryBig(String id) {
        this.id = id;
    }

    protected void finalize() {
        System.out.println("Finalizing VeryBig " + id);
    }
}

class VeryBigWeakReference extends WeakReference<VeryBig> {
    public String id;

    public VeryBigWeakReference(VeryBig big, ReferenceQueue<VeryBig> rq) {
        super(big, rq);
        this.id = big.id;
    }

    @Override
    protected void finalize() {
        System.out.println("Finalizing VeryBigWeakReference " + id);
    }
}