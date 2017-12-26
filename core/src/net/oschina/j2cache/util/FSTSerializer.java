package net.oschina.j2cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.oschina.j2cache.CacheException;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * 使用 FST 实现序列化
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class FSTSerializer implements Serializer {

	@Override
	public String name() {
		return "fst";
	}

	@Override
	public byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream out = null;
		out = new ByteArrayOutputStream();
		try (FSTObjectOutput fOut = new FSTObjectOutput(out)) {
			fOut.writeObject(obj);
			fOut.flush();
			return out.toByteArray();
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException {
		if(bytes == null || bytes.length == 0)
			return null;
		try (FSTObjectInput in = new FSTObjectInput(new ByteArrayInputStream(bytes))){
			return in.readObject();
		} catch (ClassNotFoundException e) {
			throw new CacheException(e);
		}
	}

}
