package net.oschina.j2cache;

import java.util.List;

/**
 * Cache Channel
 * @author winterlau
 */
public interface CacheChannel {

	public final static byte LEVEL_1 = 1;
	public final static byte LEVEL_2 = 2;
	
	/**
	 * 获取缓存中的数据
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @return cache object
	 */
	public CacheObject get(String region, Object key);
	
	/**
	 * 写入缓存
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @param value: Cache value
	 */
	public void set(String region, Object key, Object value);
	
	/**
	 * 写入缓存，并设置过期时间
	 * @param region
	 * @param key
	 * @param value
	 * @param expireInSec
	 */
	public void set(String region, Object key, Object value, Integer expireInSec);

	/**
	 * 删除缓存
	 * @param region:  Cache Region name
	 * @param key: Cache key
	 */
	public void evict(String region, Object key) ;

	/**
	 * 批量删除缓存
	 * @param region: Cache region name
	 * @param keys: Cache key
	 */
	@SuppressWarnings({ "rawtypes" })
	public void batchEvict(String region, List keys) ;

	/**
	 * Clear the cache
	 * @param region: Cache region name
	 */
	public void clear(String region) throws CacheException ;
	
	/**
	 * Get cache region keys
	 * @param region: Cache region name
	 * @return key list
	 */
	@SuppressWarnings("rawtypes")
	public List keys(String region) throws CacheException ;

	/**
	 * 关闭到通道的连接
	 */
	public void close() ;
}
