package net.oschina.j2cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jgroups.util.Util;

import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;
import net.sf.ehcache.CacheException;

public class SerializationUtils {

	public static byte[] serialize(Object obj) {
		return fstserialize(obj);
	}

	public static Object deserialize(byte[] bytes) {
		return fstdeserialize(bytes);
	}

	public static byte[] fstserialize(Object obj) {
		ByteArrayOutputStream out = null;
		FSTObjectOutput fout = null;
		try {
			out = new ByteArrayOutputStream();
			fout = new FSTObjectOutput(out);
			fout.writeObject(obj);
			return out.toByteArray();
		} catch (IOException e) {
			throw new CacheException(e);
		} finally {
			Util.close(out);
			Util.close(fout);
		}
	}

	public static Object fstdeserialize(byte[] bytes) {
		FSTObjectInput in = null;
		try {
			in = new FSTObjectInput(new ByteArrayInputStream(bytes));
			return in.readObject();
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			Util.close(in);
		}
	}

	public static byte[] javaserialize(Object obj) {
		ObjectOutputStream oos = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new CacheException(e);
		} finally {
			Util.close(oos);
		}
	}

	public static Object javadeserialize(byte[] bits) {
		ObjectInputStream ois = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bits);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			Util.close(ois);
		}
	}

}
