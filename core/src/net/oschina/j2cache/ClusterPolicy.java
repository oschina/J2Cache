package net.oschina.j2cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 缓存集群策略接口
 * @author Winter Lau(javayou@gmail.com)
 */
public interface ClusterPolicy {

    /**
     * 连接到集群
     */
    void connect();

    /**
     * 发送清除缓存的命令
     * @param region 区域名称
     * @param key   缓存键值
     */
    void sendEvictCmd(String region, Serializable key);

    /**
     * 发送清除整个缓存区域的命令
     * @param region 区域名称
     */
    void sendClearCmd(String region);

    /**
     * 断开集群连接
     */
    void disconnect();

    /**
     * 删除本地某个缓存条目
     * @param region 区域名称
     * @param key   缓存键值
     * @throws IOException io exception
     */
    default void evict(String region, Serializable key) throws IOException {
        if (key instanceof List)
            CacheProviderHolder.evicts(CacheProviderHolder.LEVEL_1, region, (List) key);
        else
            CacheProviderHolder.evict(CacheProviderHolder.LEVEL_1, region, key);
    }

    /**
     * 清除本地整个缓存区域
     * @param region 区域名称
     * @throws IOException io exception
     */
    default void clear(String region) throws IOException {
        CacheProviderHolder.clear(CacheProviderHolder.LEVEL_1, region);
    }
}
