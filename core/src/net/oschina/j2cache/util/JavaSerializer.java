package net.oschina.j2cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.sf.ehcache.CacheException;

/**
 * 标准的 Java 序列化
 * @author winterlau
 */
public class JavaSerializer implements Serializer {

	@Override
	public String name() {
		return "java";
	}
	
	@Override
	public byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)){
			oos.writeObject(obj);
			return baos.toByteArray();
		}
	}

	@Override
	public Object deserialize(byte[] bits) throws IOException {
		if(bits == null || bits.length == 0)
			return null;
		ByteArrayInputStream bais = new ByteArrayInputStream(bits);
		try (ObjectInputStream ois = new ObjectInputStream(bais)){
			return ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new CacheException(e);
		}
	}
	
}
