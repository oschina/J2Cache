package net.oschina.j2cache.util;

import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.J2Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 对象序列化工具包
 *
 * @author winterlau
 */
public class SerializationUtils {

    private final static Logger log = LoggerFactory.getLogger(SerializationUtils.class);
    private static Serializer g_serializer;

    public static void main(String[] args) throws IOException {
        final List<String> obj = new ArrayList<>();
        obj.addAll(Arrays.asList("OSChina.NET", "RunJS.cn", "Team@OSC", "Git@OSC", "Sonar@OSC", "PaaS@OSC"));

        g_serializer = new KryoPoolSerializer();
        byte[] bits = serialize(obj);
        for (byte b : bits) {
            System.out.print(Byte.toString(b) + " ");
        }
        System.out.println();
        System.out.println(bits.length);
        System.out.println(deserialize(bits));
    }

    static {
        String ser = J2Cache.getSerializer(); //FIXME 依赖 J2Cache ，不爽
        if (ser == null || "".equals(ser.trim()))
            g_serializer = new JavaSerializer();
        else {
            if (ser.equals("java")) {
                g_serializer = new JavaSerializer();
            } else if (ser.equals("fst")) {
                g_serializer = new FSTSerializer();
            } else if (ser.equals("kryo")) {
                g_serializer = new KryoSerializer();
            } else if (ser.equals("kryo-pool")){
                g_serializer = new KryoPoolSerializer();
            } else if(ser.equals("fst-snappy")){
                g_serializer=new FstSnappySerializer();
            } else {
                try {
                    g_serializer = (Serializer) Class.forName(ser).newInstance();
                } catch (Exception e) {
                    throw new CacheException("Cannot initialize Serializer named [" + ser + ']', e);
                }
            }
        }
        log.info("Using Serializer -> [" + g_serializer.name() + ":" + g_serializer.getClass().getName() + ']');
    }

    public static byte[] serialize(Object obj) throws IOException {
        if(obj == null)
            return null;
        return g_serializer.serialize(obj);
    }

    public static Object deserialize(byte[] bytes) throws IOException {
        if(bytes == null)
            return null;
        return g_serializer.deserialize(bytes);
    }

}
