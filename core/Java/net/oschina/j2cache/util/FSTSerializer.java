/**
 *
 */
package net.oschina.j2cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.sf.ehcache.CacheException;
import org.nustaq.serialization.FSTObjectInput;
import org.nustaq.serialization.FSTObjectOutput;

/**
 * 使用 FST 实现序列化
 * @author winterlau
 */
public class FSTSerializer implements Serializer {

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
			fout.flush();
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

}
