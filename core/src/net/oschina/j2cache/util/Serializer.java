package net.oschina.j2cache.util;

import java.io.IOException;

/**
 * 对象序列化接口
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public interface Serializer {

	/**
	 * 序列化器的名称，该方法仅用于打印日志的时候显示
	 * @return
	 */
	String name();

	/**
	 * 对象序列化到字节数组
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	byte[] serialize(Object obj) throws IOException ;

	/**
	 * 反序列化到对象
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	Object deserialize(byte[] bytes) throws IOException ;
	
}
