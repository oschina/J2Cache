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
package net.oschina.j2cache.lettuce;

import io.lettuce.core.api.StatefulConnection;
import io.lettuce.core.api.sync.RedisKeyCommands;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Redis 缓存操作封装，基于 region+_key 实现多个 Region 的缓存（
 * @author Winter Lau(javayou@gmail.com)
 */
public class LettuceGenericCache extends LettuceCache {

    public LettuceGenericCache(String namespace, String region, GenericObjectPool<StatefulConnection<String, byte[]>> pool) {
        if (region == null || region.isEmpty())
            region = "_"; // 缺省region

        super.pool = pool;
        this.namespace = namespace;
        this.region = getRegionName(region);
    }

    /**
     * 在region里增加一个可选的层级,作为命名空间,使结构更加清晰
     * 同时满足小型应用,多个J2Cache共享一个redis database的场景
     *
     * @param region
     * @return
     */
    private String getRegionName(String region) {
        if (namespace != null && !namespace.trim().isEmpty())
            region = namespace + ":" + region;
        return region;
    }

    private String _key(String key) {
        return this.region + ":" + key;
    }

    @Override
    public byte[] getBytes(String key) {
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisStringCommands<String, byte[]> cmd = (RedisStringCommands)super.sync(connection);
            return cmd.get(_key(key));
        }
    }

    @Override
    public List<byte[]> getBytes(Collection<String> keys) {
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisStringCommands<String, byte[]> cmd = (RedisStringCommands)super.sync(connection);
            return cmd.mget(keys.stream().map(k -> _key(k)).toArray(String[]::new)).stream().map(kv -> kv.hasValue()?kv.getValue():null).collect(Collectors.toList());
        }
    }

    @Override
    public void setBytes(String key, byte[] bytes) {
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisStringCommands<String, byte[]> cmd = (RedisStringCommands)super.sync(connection);
            cmd.set(_key(key), bytes);
        }
    }

    @Override
    public void setBytes(Map<String, byte[]> bytes) {
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisStringCommands<String, byte[]> cmd = (RedisStringCommands)super.sync(connection);
            cmd.mset(bytes.entrySet().stream().collect(Collectors.toMap(k -> _key(k.getKey()), Map.Entry::getValue)));
        }
    }


    /**
     * 设置缓存数据字节数组（带有效期）
     * @param key  cache key
     * @param bytes cache data
     * @param timeToLiveInSeconds cache ttl
     */
    @Override
    public void setBytes(String key, byte[] bytes, long timeToLiveInSeconds){
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisStringCommands<String, byte[]> cmd = (RedisStringCommands)super.sync(connection);
            cmd.setex(_key(key), timeToLiveInSeconds, bytes);
        }
    }

    /**
     * 批量设置带 TTL 的缓存数据
     * @param bytes  cache data
     * @param timeToLiveInSeconds cache ttl
     */
    @Override
    public void setBytes(Map<String,byte[]> bytes, long timeToLiveInSeconds) {
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisStringCommands<String, byte[]> cmd = (RedisStringCommands)super.sync(connection);
            bytes.forEach((k,v)->cmd.setex(_key(k), timeToLiveInSeconds, v));
        }
    }


    @Override
    public Collection<String> keys() {
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisKeyCommands<String, byte[]> cmd = (RedisKeyCommands)super.sync(connection);
            return cmd.keys(this.region + ":*").stream().map(k -> k.substring(this.region.length()+1)).collect(Collectors.toList());
        }
    }

    @Override
    public void evict(String... keys) {
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisKeyCommands<String, byte[]> cmd = (RedisKeyCommands)super.sync(connection);
            cmd.del(Arrays.stream(keys).map(k -> _key(k)).toArray(String[]::new));
        }
    }

    @Override
    public void clear() {
        try(StatefulConnection<String, byte[]> connection = super.connect()) {
            RedisKeyCommands<String, byte[]> cmd = (RedisKeyCommands)super.sync(connection);
            List<String> keys = cmd.keys(this.region + ":*");
            if(keys != null && keys.size() > 0)
                cmd.del(keys.stream().toArray(String[]::new));
        }
    }
}
