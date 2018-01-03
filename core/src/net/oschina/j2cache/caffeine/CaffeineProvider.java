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
package net.oschina.j2cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import net.oschina.j2cache.Cache;
import net.oschina.j2cache.CacheException;
import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.CacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine cache provider
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class CaffeineProvider implements CacheProvider {

    private final static Logger log = LoggerFactory.getLogger(CaffeineProvider.class);

    private final static String PREFIX_REGION = "region.";
    private final static String DEFAULT_REGION = "default";
    private ConcurrentHashMap<String, CaffeineCache> caches = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CacheConfig> cacheConfigs = new ConcurrentHashMap<>();

    @Override
    public String name() {
        return "caffeine";
    }

    @Override
    public Cache buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
        CaffeineCache cache = caches.get(regionName);
        if(cache == null && autoCreate){
            synchronized (CaffeineProvider.class) {
                cache = caches.get(regionName);
                if(cache == null) {
                    CacheConfig config = cacheConfigs.get(regionName);
                    if(config == null) {
                        log.info(String.format("Caffeine cache [%s] not defined, using default.", regionName));
                        config = cacheConfigs.get(DEFAULT_REGION);
                        cacheConfigs.put(regionName, config);
                    }
                    if(config == null)
                        throw new CacheException(String.format("Undefined caffeine cache region name = %s", regionName));
                    com.github.benmanes.caffeine.cache.Cache<String, Serializable> loadingCache = Caffeine.newBuilder()
                            .maximumSize(config.size)
                            .expireAfterWrite(config.expire, TimeUnit.SECONDS)
                            .removalListener((k,v, cause) -> {
                                //程序删除的缓存不做通知处理，因为上层已经做了处理
                                if(cause != RemovalCause.EXPLICIT && cause != RemovalCause.REPLACED)
                                    listener.notifyElementExpired(regionName, (String)k);
                            })
                            .build();
                    cache = new CaffeineCache(loadingCache);
                    caches.put(regionName, cache);
                }
            }
        }
        return cache;
    }

    /**
     * <p>配置示例</p>
     * <ul>
     * <li>caffeine.region.default = 10000,1h</li>
     * <li>caffeine.region.Users = 10000,1h</li>
     * <li>caffeine.region.Blogs = 80000,30m</li>
     * </ul>
     * @param props current configuration settings.
     */
    @Override
    public void start(Properties props) {
        for(String region : props.stringPropertyNames()) {
            if(!region.startsWith(PREFIX_REGION))
                continue ;
            String s_config = props.getProperty(region).trim();
            region = region.substring(PREFIX_REGION.length());
            CacheConfig cfg = CacheConfig.parse(s_config);
            if(cfg == null) {
                log.warn(String.format("Illegal caffeine cache config [%s=%s]", region, s_config));
                continue;
            }
            cacheConfigs.put(region, cfg);
        }
    }

    @Override
    public void stop() {

    }

    /**
     * 缓存配置
     */
    private static class CacheConfig {

        private long size;
        private long expire;

        public static CacheConfig parse(String cfg) {
            CacheConfig cacheConfig = null;
            String[] cfgs = cfg.split(",");
            if(cfgs.length == 2){
                cacheConfig = new CacheConfig();
                String sSize = cfgs[0].trim();
                String sExpire = cfgs[1].trim();
                cacheConfig.size = Long.parseLong(sSize);
                char unit = Character.toLowerCase(sExpire.charAt(sExpire.length()-1));
                cacheConfig.expire = Long.parseLong(sExpire.substring(0, sExpire.length() - 1));
                switch(unit){
                    case 's'://seconds
                        break;
                    case 'm'://minutes
                        cacheConfig.expire *= 60;
                    case 'h':
                        cacheConfig.expire *= 3600;
                    case 'd':
                        cacheConfig.expire *= 86400;
                }
            }
            return cacheConfig;
        }

    }

}
