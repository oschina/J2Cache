package net.oschina.j2cache.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (Output output = new Output(baos);){
			kryo.writeClassAndObject(output, obj);
			output.flush();
			return baos.toByteArray();
		}
	}

	@Override
	public Object deserialize(byte[] bits) throws IOException {
		if(bits == null || bits.length == 0)
			return null;
		try (Input ois = new Input(new ByteArrayInputStream(bits))){
			return kryo.readClassAndObject(ois);
		}
	}
	
}
