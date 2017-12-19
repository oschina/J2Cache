package net.oschina.j2cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Cache Channel
 * @author winterlau
 */
public interface CacheChannel extends Closeable {

    Log log = LogFactory.getLog(CacheChannel.class);

	byte LEVEL_1 = 1;
	byte LEVEL_2 = 2;

    void sendClearCmd(String region);

    void sendEvictCmd(String region, Object key);

    /**
	 * 获取缓存中的数据
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @return cache object
	 */
	default CacheObject get(String region, Serializable key) throws IOException {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if(region!=null && key != null){
			obj.setValue(CacheProviderHolder.get(LEVEL_1, region, key));
			if(obj.getValue() == null) {
				obj.setValue(CacheProviderHolder.get(LEVEL_2, region, key));
				if(obj.getValue() != null){
					obj.setLevel(LEVEL_2);
					CacheProviderHolder.set(LEVEL_1, region, key, obj.getValue());
				}
			}
			else
				obj.setLevel(LEVEL_1);
		}
		return obj;
	}
	
	/**
	 * 写入缓存
	 * @param region: Cache Region name
	 * @param key: Cache key
	 * @param value: Cache value
	 */
	default void set(String region, Serializable key, Serializable value) throws IOException {
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
                CacheProviderHolder.set(LEVEL_1, region, key, value);
                CacheProviderHolder.set(LEVEL_2, region, key, value);
                sendEvictCmd(region, key);//清除原有的一级缓存的内容
            }
        }
        //log.info("write data to cache region="+region+",key="+key+",value="+value);
    }

	/**
	 * 删除缓存
	 * @param region:  Cache Region name
	 * @param key: Cache key
	 */
	default void evict(String region, Serializable key) throws IOException {
        CacheProviderHolder.evict(LEVEL_1, region, key); //删除一级缓存
        CacheProviderHolder.evict(LEVEL_2, region, key); //删除二级缓存
        sendEvictCmd(region, key); //发送广播
    }

	/**
	 * 批量删除缓存
	 * @param region: Cache region name
	 * @param keys: Cache key
	 */
	default void evicts(String region, List<Serializable> keys) throws IOException {
        CacheProviderHolder.evicts(LEVEL_1, region, keys);
        CacheProviderHolder.evicts(LEVEL_2, region, keys);
        sendEvictCmd(region, keys);
    }

	/**
	 * Clear the cache
	 * @param region: Cache region name
	 */
	default void clear(String region) throws IOException {
        CacheProviderHolder.clear(LEVEL_1, region);
        CacheProviderHolder.clear(LEVEL_2, region);
        sendClearCmd(region);
    }
	
	/**
	 * Get cache region keys
	 * @param region: Cache region name
	 * @return key list
	 */
	default Set<Serializable> keys(String region) throws IOException {
        return CacheProviderHolder.keys(LEVEL_1, region);
    }

	/**
	 * 关闭到通道的连接
	 */
	void close();
}
