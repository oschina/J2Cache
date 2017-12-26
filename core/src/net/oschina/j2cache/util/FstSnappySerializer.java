package net.oschina.j2cache.util;

import java.io.IOException;

import org.xerial.snappy.Snappy;

/**
 * 在 @FSTSerializer 的基础上增加 snappy ，也就是压缩
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
		return Snappy.compress(inner.serialize(obj));
	}

	@Override
	public Object deserialize(byte[] bytes) throws IOException {
		if (bytes == null || bytes.length == 0)
			return null;
		return inner.deserialize(Snappy.uncompress(bytes));
	}
}
