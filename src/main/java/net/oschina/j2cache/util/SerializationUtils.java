package net.oschina.j2cache.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.ruedigermoeller.serialization.FSTObjectInput;
import de.ruedigermoeller.serialization.FSTObjectOutput;

import net.sf.ehcache.CacheException;

public class SerializationUtils {

	public static byte[] serialize(Object obj) {
		ByteArrayOutputStream out = null;
		FSTObjectOutput fout = null;
		try {
			out = new ByteArrayOutputStream();
			fout = new FSTObjectOutput(out);
			fout.writeObject(obj);
			fout.flush();
			return out.toByteArray();
		} catch (IOException e) {
			throw new CacheException(e);
		} finally {
			try {
				if (null != out) {
					out.close();
					out = null;
				}
				if (null != fout) {
					fout.close();
					out = null;
				}
			} catch (IOException e) {
			}
		}
	}

	public static Object deserialize(byte[] bytes) {
		FSTObjectInput in = null;
		try {
			in = new FSTObjectInput(new ByteArrayInputStream(bytes));
			return in.readObject();
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			try {
				if (null != in) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
			}
		}
	}

	public static byte[] sserialize(Object obj) {
		ObjectOutputStream oos = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			return baos.toByteArray();
		} catch (IOException e) {
			throw new CacheException(e);
		} finally {
			if (oos != null)
				try {
					oos.close();
				} catch (IOException e) {
				}
		}
	}

	public static Object sdeserialize(byte[] bits) {
		ObjectInputStream ois = null;
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bits);
			ois = new ObjectInputStream(bais);
			return ois.readObject();
		} catch (Exception e) {
			throw new CacheException(e);
		} finally {
			if (ois != null)
				try {
					ois.close();
				} catch (IOException e) {
				}
		}
	}

}
