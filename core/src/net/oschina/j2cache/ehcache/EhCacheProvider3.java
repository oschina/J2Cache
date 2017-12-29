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
package net.oschina.j2cache.ehcache;

import net.oschina.j2cache.CacheExpiredListener;
import net.oschina.j2cache.CacheProvider;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EhCache 2.x 缓存管理器的封装，用来管理多个缓存区域
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class EhCacheProvider3 implements CacheProvider {

    private final static Logger log = LoggerFactory.getLogger(EhCacheProvider3.class);

    private CacheManager cacheManager;
    private ConcurrentHashMap<String, EhCache3> _CacheManager = new ConcurrentHashMap<>();

    @Override
    public String name() {
        return "ehcache3";
    }

    @Override
    public EhCache3 buildCache(String regionName, boolean autoCreate, CacheExpiredListener listener) {
        EhCache3 ehcache = _CacheManager.get(regionName);
        if(ehcache == null && autoCreate){
            synchronized(EhCacheProvider.class){
                ehcache = _CacheManager.get(regionName);
                if(ehcache == null){
                    org.ehcache.Cache cache = cacheManager.getCache(regionName, Serializable.class, Serializable.class);
                    if (cache == null) {
                        int heapSize = 1000;
                        log.warn("Could not find configuration [" + regionName + "]; using defaults.");
                        CacheConfiguration<Serializable, Serializable> cacheCfg = CacheConfigurationBuilder.newCacheConfigurationBuilder(Serializable.class, Serializable.class, ResourcePoolsBuilder.heap(heapSize)).build();
                        cache = cacheManager.createCache(regionName, cacheCfg);
                        log.debug(String.format("Started Ehcache region [%s] with heap size: %d", regionName, heapSize));
                    }
                    ehcache = new EhCache3(regionName, cache, listener);
                    _CacheManager.put(regionName, ehcache);
                }
            }
        }
        return ehcache;
    }

    @Override
    public void start(Properties props) {
        String configXml = props.getProperty("configXml");
        if(configXml == null || configXml.trim().length() == 0)
            configXml = "/ehcache3.xml";
        URL myUrl = getClass().getResource(configXml);
        Configuration xmlConfig = new XmlConfiguration(myUrl);
        cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
        cacheManager.init();
    }

    @Override
    public void stop() {
        if (cacheManager != null) {
            cacheManager.close();
            _CacheManager.clear();
            cacheManager = null;
        }
    }
}
