package net.oschina.j2cache.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oschina.j2cache.CacheManager;
import net.sf.ehcache.CacheException;

/**
 * 对象序列化工具包
 * @author winterlau
 */
public class SerializationUtils {

	private final static Logger log = LoggerFactory.getLogger(SerializationUtils.class);
	private static Serializer g_ser ;
	
	public static void main(String[] args) throws IOException {
		
		List<String> obj = Arrays.asList("OSChina.NET","RunJS.cn","Team@OSC", "Git@OSC", "Sonar@OSC", "PaaS@OSC");
		byte[] bits = serialize(obj);
		for(byte b : bits){
			System.out.print(Byte.toString(b)+" ");
		}
		System.out.println();
		System.out.println(bits.length);
		System.out.println(deserialize(bits));
		
	}
	
	static {
		String ser = CacheManager.getSerializer();
		if(ser == null || "".equals(ser.trim()))
			g_ser = new JavaSerializer();
		else{
			switch(ser){
			case "java":
				g_ser = new JavaSerializer();
				break;
			case "fst":
				g_ser = new FSTSerializer();
				break;
			case "kryo":
				g_ser = new KryoSerializer();
				break;
			default:
				try {
					g_ser = (Serializer)Class.forName(ser).newInstance();
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
