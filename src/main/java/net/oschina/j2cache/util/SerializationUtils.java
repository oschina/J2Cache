package net.oschina.j2cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import net.sf.ehcache.CacheException;

/**
 * 对象序列化工具包
 * @author winterlau
 */
public class SerializationUtils {

	public static byte[] serialize(Object obj) throws IOException {
		return fstserialize(obj);
	}

	public static Object deserialize(byte[] bytes) throws IOException {
		return fstdeserialize(bytes);
	}

	public static byte[] fstserialize(Object obj) throws IOException {
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

	public static Object fstdeserialize(byte[] bytes) throws IOException {
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

	public static byte[] javaserialize(Object obj) throws IOException {
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

	public static Object javadeserialize(byte[] bits) throws IOException {
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

}
