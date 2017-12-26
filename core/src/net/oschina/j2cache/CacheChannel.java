package net.oschina.j2cache;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Cache Channel, the J2Cache methods explored to developers
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public abstract class CacheChannel implements Closeable {

	/**
	 * <p>Just for Inner Use.</p>
	 *
	 * <p>To clear the whole region when received this event .</p>
	 *
	 * @param region
	 */
	protected abstract void sendClearCmd(String region);

	/**
	 * <p>Just for Inner Use.</p>
	 *
	 * <p>To remove cached data when received this event .</p>
	 *
	 * @param region Cache region name
	 * @param key	Cache data key
	 */
	protected abstract void sendEvictCmd(String region, Serializable key);

    /**
	 * Get CacheObject from J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @return cache object
	 * @throws IOException io exception
	 */
	public CacheObject get(String region, Serializable key) throws IOException {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if(region!=null && key != null){
			obj.setValue(CacheProviderHolder.get(CacheProviderHolder.LEVEL_1, region, key));
			if(obj.getValue() == null) {
				obj.setValue(CacheProviderHolder.get(CacheProviderHolder.LEVEL_2, region, key));
				if(obj.getValue() != null){
					obj.setLevel(CacheProviderHolder.LEVEL_2);
					CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, obj.getValue());
				}
			}
			else
				obj.setLevel(CacheProviderHolder.LEVEL_1);
		}
		return obj;
	}

	/**
	 * Get User Original Object from J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @return	User object
	 * @throws IOException io exception
	 */
	public Serializable getRawObject(String region, Serializable key) throws IOException {
		CacheObject cache = get(region, key);
		return (cache != null)?cache.getValue():null;
	}
	
	/**
	 * Write data to J2Cache
	 *
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @param value: Cache value
	 * @throws IOException io exception
	 */
	public void set(String region, Serializable key, Serializable value) throws IOException {
        if(region!=null && key != null){
            if(value == null)
                evict(region, key);
            else{
                //分几种情况
                //Object obj1 = CacheProviderHolder.get(LEVEL_1, region, key);
                //Object obj2 = CacheProviderHolder.get(LEVEL_2, region, key);
                //1. L1 和 L2 都没有
                //2. L1 有 L2 没有（这种情况不存在，除非是写 L2 的时候失败
                //3. L1 没有，L2 有
                //4. L1 和 L2 都有
                //有可能引起缓存不同步_sendEvictCmd(region, key);//清除原有的一级缓存的内容
                CacheProviderHolder.set(CacheProviderHolder.LEVEL_1, region, key, value);
                CacheProviderHolder.set(CacheProviderHolder.LEVEL_2, region, key, value);
                sendEvictCmd(region, key);//清除原有的一级缓存的内容
            }
        }
        //log.info("write data to cache region="+region+",key="+key+",value="+value);
    }

	/**
	 * Remove cached data in J2Cache
	 *
	 * @param region:  Cache Region name
	 * @param key: Cache key
	 * @throws IOException io exception
	 */
	public void evict(String region, Serializable key) throws IOException {
        CacheProviderHolder.evict(CacheProviderHolder.LEVEL_1, region, key); //删除一级缓存
        CacheProviderHolder.evict(CacheProviderHolder.LEVEL_2, region, key); //删除二级缓存
        sendEvictCmd(region, key); //发送广播
    }

	/**
	 * Remote some cached data in J2Cache
	 *
	 * @param region: Cache region name
	 * @param keys: Cache key
	 * @throws IOException io exception
	 */
	public void evicts(String region, List<Serializable> keys) throws IOException {
        CacheProviderHolder.evicts(CacheProviderHolder.LEVEL_1, region, keys);
        CacheProviderHolder.evicts(CacheProviderHolder.LEVEL_2, region, keys);
        //FIXME 效率低下
        keys.forEach(key -> sendEvictCmd(region, key));
    }

	/**
	 * Clear the cache
	 *
	 * @param region: Cache region name
	 * @throws IOException io exception
	 */
	public void clear(String region) throws IOException {
        CacheProviderHolder.clear(CacheProviderHolder.LEVEL_1, region);
        CacheProviderHolder.clear(CacheProviderHolder.LEVEL_2, region);
        sendClearCmd(region);
    }
	
	/**
	 * Get cache region keys
	 *
	 * @param region: Cache region name
	 * @return key list
	 * @throws IOException io exception
	 */
	public Set<Serializable> keys(String region) throws IOException {
        return CacheProviderHolder.keys(CacheProviderHolder.LEVEL_1, region);
    }

	/**
	 * Close J2Cache
	 */
	public abstract void close();
}
