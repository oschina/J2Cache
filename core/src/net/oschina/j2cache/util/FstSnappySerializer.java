package net.oschina.j2cache.util;

import java.io.IOException;

import net.oschina.j2cache.CacheException;
import org.xerial.snappy.Snappy;

/**
 * FSTSerializer增加snappy
 */
public class FstSnappySerializer implements Serializer {

	private final Serializer inner;

	public FstSnappySerializer() {
		this(new FSTSerializer());
	}

	public FstSnappySerializer(Serializer innerSerializer) {
		this.inner = innerSerializer;
	}
	
	
	@Override
	public String name() {
		return "fst-snappy";
	}
	
	@Override
	public byte[] serialize(Object obj) throws IOException {
		try {
			return Snappy.compress(inner.serialize(obj));
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	@Override
	public Object deserialize(byte[] bytes) {
		if (bytes == null || bytes.length == 0)
			return null;
		try {
			return inner.deserialize(Snappy.uncompress(bytes));
		} catch (Exception e) {
			throw new CacheException(e);
		}
	}
}
