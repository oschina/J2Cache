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
package net.oschina.j2cache.session;

import redis.clients.jedis.JedisPoolConfig;

import java.io.Closeable;
import java.util.Properties;

/**
 * 缓存封装入口
 * @author Winter Lau(javayou@gmail.com)
 */
public class CacheFacade implements Closeable, AutoCloseable, CacheExpiredListener {

    private CaffeineCache cache1;
    private RedisCache cache2;

    public CacheFacade(String region, int maxSizeInMemory, int maxAge, Properties redisConf) {
        this.cache1 = new CaffeineCache(region, maxSizeInMemory, maxAge, this);

        JedisPoolConfig poolConfig = RedisUtils.newPoolConfig(redisConf, null);

        String hosts = redisConf.getProperty("hosts");
        String mode = redisConf.getProperty("mode");
        String clusterName = redisConf.getProperty("cluster_name");
        String password = redisConf.getProperty("password");
        int database = Integer.parseInt(redisConf.getProperty("database"));

        //long ct = System.currentTimeMillis();

        RedisClient redisClient = new RedisClient.Builder()
                .mode(mode)
                .hosts(hosts)
                .password(password)
                .cluster(clusterName)
                .database(database)
                .poolConfig(poolConfig).newClient();

        this.cache2 = new RedisCache(null, redisClient);

    }

    public SessionObject getSession() {
        return null;
    }

    public void saveSession() {

    }

    @Override
    public void notifyElementExpired(String key) {

    }

    @Override
    public void close() {
        this.cache1.close();
        this.cache2.close();
    }

}
