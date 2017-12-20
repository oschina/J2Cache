package net.oschina.j2cache;

import java.io.Serializable;

/**
 * 侦听缓存中的某个记录超时
 * @author winterlau
 */
public interface CacheExpiredListener {

	/**
	 * 当缓存中的某个对象超时被清除的时候触发
	 * @param region: Cache region name
	 * @param key: cache key
	 */
	void notifyElementExpired(String region, Serializable key) ;

}
