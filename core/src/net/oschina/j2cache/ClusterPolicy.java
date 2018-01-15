/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache;

import java.io.IOException;
import java.util.Properties;

/**
 * 缓存集群策略接口
 * @author Winter Lau(javayou@gmail.com)
 */
public interface ClusterPolicy {

    /**
     * 连接到集群
     * @param props j2cache 配置信息
     */
    void connect(Properties props);

    /**
     * 发送清除缓存的命令
     * @param region 区域名称
     * @param keys   缓存键值
     */
    void sendEvictCmd(String region, String...keys);

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
     * @param keys   缓存键值
     */
    default void evict(String region, String... keys) {
        CacheProviderHolder.getLevel1Cache(region).evict(keys);
    }

    /**
     * 清除本地整个缓存区域
     * @param region 区域名称
     */
    default void clear(String region) {
        CacheProviderHolder.getLevel1Cache(region).clear();
    }
}
