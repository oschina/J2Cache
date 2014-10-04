package net.oschina.j2cache.util;

import net.oschina.j2cache.CacheManager;
import net.sf.ehcache.CacheException;
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
    private static Serializer g_ser;

    public static void main(String[] args) throws IOException {
        final List<String> obj = new ArrayList<String>();
        obj.addAll(Arrays.asList("OSChina.NET", "RunJS.cn", "Team@OSC", "Git@OSC", "Sonar@OSC", "PaaS@OSC"));

        g_ser = new KryoPoolSerializer();
        byte[] bits = serialize(obj);
        for (byte b : bits) {
            System.out.print(Byte.toString(b) + " ");
        }
        System.out.println();
        System.out.println(bits.length);
        System.out.println(deserialize(bits));
    }

    static {
        String ser = CacheManager.getSerializer();
        if (ser == null || "".equals(ser.trim()))
            g_ser = new JavaSerializer();
        else {
            if (ser.equals("java")) {
                g_ser = new JavaSerializer();
            } else if (ser.equals("fst")) {
                g_ser = new FSTSerializer();
            } else if (ser.equals("kryo")) {
                g_ser = new KryoSerializer();
            } else if (ser.equals("kryo_pool_ser")){
            	g_ser = new KryoPoolSerializer();
            } else {
                try {
                    g_ser = (Serializer) Class.forName(ser).newInstance();
                } catch (Exception e) {
                    throw new CacheException("Cannot initialize Serializer named [" + ser + ']', e);
                }
            }
        }
        log.info("Using Serializer -> [" + g_ser.name() + ":" + g_ser.getClass().getName() + ']');
    }

    public static byte[] serialize(Object obj) throws IOException {
        return g_ser.serialize(obj);
    }

    public static Object deserialize(byte[] bytes) throws IOException {
        return g_ser.deserialize(bytes);
    }

}
