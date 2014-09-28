/**
 * 
 */
package net.oschina.j2cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sf.ehcache.CacheException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 使用 Kryo 实现序列化
 * @author winterlau
 */
public class KryoSerializer implements Serializer {

	private final static Kryo kryo = new Kryo();
	
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
	
}
