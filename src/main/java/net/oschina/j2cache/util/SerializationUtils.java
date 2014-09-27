package net.oschina.j2cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import net.oschina.j2cache.CacheManager;
import net.sf.ehcache.CacheException;

/**
 * 对象序列化工具包
 * @author winterlau
 */
public class SerializationUtils {

	private final static Logger log = LoggerFactory.getLogger(SerializationUtils.class);
	private static Serializer g_ser = null;
	
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
	
	public static byte[] serialize(Object obj) throws IOException {
		return getSerializer().serialize(obj);
	}

	public static Object deserialize(byte[] bytes) throws IOException {
		return getSerializer().deserialize(bytes);
	}

	private static Serializer getSerializer() {
		if(g_ser == null) {
			String ser = CacheManager.getSerializer();
			if(ser == null || "".equals(ser.trim()))
				g_ser = java_ser;
			else{
				switch(ser){
				case "java":
					g_ser = java_ser;
					break;
				case "fst":
					g_ser = fst_ser;
					break;
				case "kryo":
					g_ser = kryo_ser;
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
		return g_ser;
	}
	
	private final static Serializer fst_ser = new Serializer() {

		@Override
		public String name() {
			return "fst";
		}
		
		@Override
		public byte[] serialize(Object obj) throws IOException {
			ByteArrayOutputStream out = null;
			FSTObjectOutput fout = null;
			try {
				out = new ByteArrayOutputStream();
				fout = new FSTObjectOutput(out);
				fout.writeObject(obj);
				return out.toByteArray();
			} finally {
				if(fout != null)
				try {
					fout.close();
				} catch (IOException e) {}
			}
		}

		@Override
		public Object deserialize(byte[] bytes) throws IOException {
			if(bytes == null || bytes.length == 0)
				return null;
			FSTObjectInput in = null;
			try {
				in = new FSTObjectInput(new ByteArrayInputStream(bytes));
				return in.readObject();
			} catch (ClassNotFoundException e) {
				throw new CacheException(e);
			} finally {
				if(in != null)
				try {
					in.close();
				} catch (IOException e) {}
			}
		}

	};
	
	private final static Serializer java_ser = new Serializer() {

		@Override
		public String name() {
			return "java";
		}
		
		@Override
		public byte[] serialize(Object obj) throws IOException {
			ObjectOutputStream oos = null;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				oos = new ObjectOutputStream(baos);
				oos.writeObject(obj);
				return baos.toByteArray();
			} finally {
				if(oos != null)
				try {
					oos.close();
				} catch (IOException e) {}
			}
		}

		@Override
		public Object deserialize(byte[] bits) throws IOException {
			if(bits == null || bits.length == 0)
				return null;
			ObjectInputStream ois = null;
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(bits);
				ois = new ObjectInputStream(bais);
				return ois.readObject();
			} catch (ClassNotFoundException e) {
				throw new CacheException(e);
			} finally {
				if(ois != null)
				try {
					ois.close();
				} catch (IOException e) {}
			}
		}
		
	};
	
	private final static Serializer kryo_ser = new Serializer() {
		
		Kryo kryo = new Kryo();
		
		@Override
		public String name() {
			return "kryo";
		}

		@Override
		public byte[] serialize(Object obj) throws IOException {
			Output output = null;
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				output = new Output(baos);
				kryo.writeClassAndObject(output, obj);
				output.flush();
				return baos.toByteArray();
			}finally{
				if(output != null)
					output.close();
			}
		}

		@Override
		public Object deserialize(byte[] bits) throws IOException {
			if(bits == null || bits.length == 0)
				return null;
			Input ois = null;
			try {
				ByteArrayInputStream bais = new ByteArrayInputStream(bits);
				ois = new Input(bais);
				return kryo.readClassAndObject(ois);
			} catch (Exception e) {
				throw new CacheException(e);
			} finally {
				if(ois != null)
					ois.close();
			}
		}
		
	};

}
