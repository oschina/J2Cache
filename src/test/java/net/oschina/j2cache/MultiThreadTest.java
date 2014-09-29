package net.oschina.j2cache;

import net.oschina.j2cache.util.SerializationUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author 石头哥哥
 *         </P>
 *         Date:   2014/9/29
 *         </P>
 *         Time:   9:20
 *         </P>
 *         Package: j2cache
 *         </P>
 *         <p/>
 *         注解：      多线程测试 kryo -- pool
 */
public class MultiThreadTest {


    /**
     * #########################################
     * # Cache Serialization Provider
     * # values:
     * # fst -> fast-serialization
     * # java -> java standard
     * # kryo ->kryo_pool_ser or kryo
     * # [classname implements Serializer]
     * #########################################
     * <p/>
     * 调试配置 ：cache.serialization = kryo_pool_ser
     *
     * @param args
     */


    public static void main(String[] args) {

        /**
         * 使用kyro ，应该至少有一个默认的构造函数，否则无法反序列化。
         *
         *  List<String> obj =Arrays.asList("OSChina.NET", "RunJS.cn", "Team@OSC", "Git@OSC", "Sonar@OSC", "PaaS@OSC");
         *  obj  instance of interface   ,  所以是无法反序列化的  ，这点要注意 。
         */

       final List<String> obj =Arrays.asList("OSChina.NET", "RunJS.cn", "Team@OSC", "Git@OSC", "Sonar@OSC", "PaaS@OSC");

        CacheManager.initCacheProvider(null);
        //final List<String> obj = new ArrayList<String>();
        obj.addAll(Arrays.asList("OSChina.NET", "RunJS.cn", "Team@OSC", "Git@OSC", "Sonar@OSC", "PaaS@OSC"));
        /**
         * 多线程测试
         */
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i != 10000; ++i) {

                    System.out.println(Thread.currentThread().getName());

                    byte[] bits = new byte[0];
                    try {
                        bits = SerializationUtils.serialize(obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (byte b : bits) {
                        System.out.print(Byte.toString(b) + " ");
                    }
                    try {
                        SerializationUtils.deserialize(bits);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i != 10000; ++i) {
                    System.out.println(Thread.currentThread().getName());
                    byte[] bits = new byte[0];
                    try {
                        bits = SerializationUtils.serialize(obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (byte b : bits) {
                        System.out.print(Byte.toString(b) + " ");
                    }
                    try {
                        SerializationUtils.deserialize(bits);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i != 10000; ++i) {
                    System.out.println(Thread.currentThread().getName());
                    byte[] bits = new byte[0];
                    try {
                        bits = SerializationUtils.serialize(obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (byte b : bits) {
                        System.out.print(Byte.toString(b) + " ");
                    }
                    try {
                        SerializationUtils.deserialize(bits);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i != 10000; ++i) {
                    System.out.println(Thread.currentThread().getName());
                    byte[] bits = new byte[0];
                    try {
                        bits = SerializationUtils.serialize(obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (byte b : bits) {
                        System.out.print(Byte.toString(b) + " ");
                    }
                    try {
                        SerializationUtils.deserialize(bits);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        t1.start();
        t2.start();
        t3.start();
        t4.start();

    }


}
