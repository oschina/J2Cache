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
package net.oschina.j2cache.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * <p>封装各种模式的 Redis 客户端成统一接口</p>
 *
 * <p>Jedis 接口设计真操蛋</p>
 *
 * @author Winter Lau (javayou@gmail.com)
 */
public class RedisClient implements Closeable {

    private final static Logger log = LoggerFactory.getLogger(RedisClient.class);

    private final static int CONNECT_TIMEOUT = 5000;    //Redis连接超时时间
    private final static int SO_TIMEOUT = 5000;
    private final static int MAX_ATTEMPTS = 3;

    private ThreadLocal<BinaryJedisCommands> clients;

    private JedisCluster cluster;
    private JedisPool single;
    private JedisSentinelPool sentinel;
    private ShardedJedisPool sharded;
    private String redisPassword;

    /**
     * RedisClient 构造器
     */
    public static class Builder {
        private String mode;
        private String hosts;
        private String password;
        private String cluster;
        private int database;
        private JedisPoolConfig poolConfig;

        public Builder(){}

        public Builder mode(String mode){
            if(mode == null || mode.trim().length() == 0)
                this.mode = "single";
            else
                this.mode = mode;
            return this;
        }
        public Builder hosts(String hosts){
            if(hosts == null || hosts.trim().length() == 0)
                this.hosts = "127.0.0.1:6379";
            else
                this.hosts = hosts;
            return this;
        }
        public Builder password(String password){
            if(password != null && password.trim().length() > 0)
                this.password = password;
            return this;
        }
        public Builder cluster(String cluster) {
            if(cluster == null || cluster.trim().length() == 0)
                this.cluster = "j2cache";
            else
                this.cluster = cluster;
            return this;
        }
        public Builder database(int database){
            this.database = database;
            return this;
        }
        public Builder poolConfig(JedisPoolConfig poolConfig){
            this.poolConfig = poolConfig;
            return this;
        }
        public RedisClient newClient() {
            return new RedisClient(mode, hosts, password, cluster, database, poolConfig);
        }
    }


    /**
     * 各种模式 Redis 客户端的封装
     * @param mode Redis 服务器运行模式
     * @param hosts Redis 主机连接信息
     * @param password  Redis 密码（如果有的话）
     * @param cluster_name  集群名称
     * @param database 数据库
     * @param poolConfig    连接池配置
     */
    private RedisClient(String mode, String hosts, String password, String cluster_name, int database, JedisPoolConfig poolConfig) {
        this.redisPassword = (password != null && password.trim().length() > 0)? password.trim(): null;
        this.clients = new ThreadLocal<>();
        switch(mode){
            case "sentinel":
                Set<String> nodes = new HashSet<>();
                for(String node : hosts.split(","))
                    nodes.add(node);
                this.sentinel = new JedisSentinelPool(cluster_name, nodes, poolConfig, CONNECT_TIMEOUT, password, database);
                break;
            case "cluster":
                Set<HostAndPort> hps = new HashSet<>();
                for(String node : hosts.split(",")){
                    String[] infos = node.split(":");
                    String host = infos[0];
                    int port = (infos.length > 1)?Integer.parseInt(infos[1]):6379;
                    hps.add(new HostAndPort(host, port));
                }
                this.cluster = new JedisCluster(hps, CONNECT_TIMEOUT, SO_TIMEOUT, MAX_ATTEMPTS, password, poolConfig);
                break;
            case "sharded":
                List<JedisShardInfo> shards = new ArrayList<>();
                try {
                    for(String node : hosts.split(","))
                        shards.add(new JedisShardInfo(new URI(node)));
                } catch (URISyntaxException e) {
                    throw new JedisConnectionException(e);
                }
                this.sharded = new ShardedJedisPool(poolConfig, shards);
                break;
            default:
                for(String node : hosts.split(",")) {
                    String[] infos = node.split(":");
                    String host = infos[0];
                    int port = (infos.length > 1)?Integer.parseInt(infos[1]):6379;
                    this.single = new JedisPool(poolConfig, host, port, CONNECT_TIMEOUT, password, database);
                    break;
                }
                if(!"single".equalsIgnoreCase(mode))
                    log.warn("Redis mode [" + mode + "] not defined. Using 'single'.");
                break;
        }
    }

    /**
     * 获取客户端接口
     * @return 返回基本的 Jedis 二进制命令接口
     */
    public BinaryJedisCommands get() {
        BinaryJedisCommands client = clients.get();
        if(client == null) {
            if (single != null)
                client = single.getResource();
            else if (sentinel != null)
                client = sentinel.getResource();
            else if (sharded != null)
                client = sharded.getResource();
            else if (cluster != null)
                client = toBinaryJedisCommands(cluster);
            clients.set(client);
        }
        return client;
    }

    /**
     * 释放 Redis 连接
     */
    public void release() {
        BinaryJedisCommands client = clients.get();
        if(client != null) {
            //JedisCluster 会自动释放连接
            if(client instanceof Closeable && !(client instanceof JedisCluster)) {
                try {
                    ((Closeable) client).close();
                } catch(IOException e) {
                    log.error("Failed to release jedis connection.", e);
                }
            }
            else
                log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!");
            clients.remove();
        }
    }

    /**
     * 订阅
     * @param jedisPubSub 接受订阅消息的实例
     * @param channels 订阅的频道名称
     */
    public void subscribe(BinaryJedisPubSub jedisPubSub, byte[]... channels) {
        if(cluster != null)
            cluster.subscribe(jedisPubSub, channels);
        else if(single != null)
            single.getResource().subscribe(jedisPubSub, channels);
        else if(sentinel != null)
            sentinel.getResource().subscribe(jedisPubSub, channels);
        if(sharded != null)
            sharded.getResource().getAllShards().forEach(node -> node.subscribe(jedisPubSub, channels));
    }

    /**
     * 发布订阅消息
     * @param channel 订阅频道
     * @param bytes 消息数据
     */
    public void publish(byte[] channel, byte[] bytes) {
        try {
            if (cluster != null)
                cluster.publish(channel, bytes);

            else if (single != null) {
                try(Jedis jedis = single.getResource()) {
                    jedis.publish(channel, bytes);
                }
            }
            else if (sentinel != null) {
                try (Jedis jedis = sentinel.getResource()) {
                    jedis.publish(channel, bytes);
                }
            }
            else if (sharded != null) {
                try (ShardedJedis jedis = sharded.getResource()) {
                    jedis.getAllShards().forEach(node -> {
                        try {
                            node.publish(channel, bytes);
                        } finally {
                            node.close();
                        }
                    });
                }
            }
        } finally {
            release();
        }
    }

    @Override
    public void close() throws IOException {
        if(single != null)
            single.close();
        if(sentinel != null)
            sentinel.close();
        if(cluster != null)
            cluster.close();
        if(sharded != null)
            sharded.close();
    }

    public JedisCommands toJedisCommands(JedisCluster cluster) {
        return new JedisCommands() {
            @Override
            public String set(String s, String s1) {
                return cluster.set(s, s1);
            }

            @Override
            public String set(String s, String s1, String s2, String s3, long l) {
                return cluster.set(s, s1, s2, s3, l);
            }

            @Override
            public String set(String s, String s1, String s2) {
                return cluster.set(s, s1, s2);
            }

            @Override
            public String get(String s) {
                return cluster.get(s);
            }

            @Override
            public Boolean exists(String s) {
                return cluster.exists(s);
            }

            @Override
            public Long persist(String s) {
                return cluster.persist(s);
            }

            @Override
            public String type(String s) {
                return cluster.type(s);
            }

            @Override
            public Long expire(String s, int i) {
                return cluster.expire(s, i);
            }

            @Override
            public Long pexpire(String s, long l) {
                return cluster.pexpire(s, l);
            }

            @Override
            public Long expireAt(String s, long l) {
                return cluster.expireAt(s, l);
            }

            @Override
            public Long pexpireAt(String s, long l) {
                return cluster.pexpireAt(s, l);
            }

            @Override
            public Long ttl(String s) {
                return cluster.ttl(s);
            }

            @Override
            public Long pttl(String s) {
                return cluster.pttl(s);
            }

            @Override
            public Boolean setbit(String s, long l, boolean b) {
                return cluster.setbit(s, l, b);
            }

            @Override
            public Boolean setbit(String s, long l, String s1) {
                return cluster.setbit(s, l, s1);
            }

            @Override
            public Boolean getbit(String s, long l) {
                return cluster.getbit(s, l);
            }

            @Override
            public Long setrange(String s, long l, String s1) {
                return cluster.setrange(s, l, s1);
            }

            @Override
            public String getrange(String s, long l, long l1) {
                return cluster.getrange(s, l, l1);
            }

            @Override
            public String getSet(String s, String s1) {
                return cluster.getSet(s, s1);
            }

            @Override
            public Long setnx(String s, String s1) {
                return cluster.setnx(s, s1);
            }

            @Override
            public String setex(String s, int i, String s1) {
                return cluster.setex(s, i, s1);
            }

            @Override
            public String psetex(String s, long l, String s1) {
                return cluster.psetex(s, l, s1);
            }

            @Override
            public Long decrBy(String s, long l) {
                return cluster.decrBy(s, l);
            }

            @Override
            public Long decr(String s) {
                return cluster.decr(s);
            }

            @Override
            public Long incrBy(String s, long l) {
                return cluster.incrBy(s, l);
            }

            @Override
            public Double incrByFloat(String s, double v) {
                return cluster.incrByFloat(s, v);
            }

            @Override
            public Long incr(String s) {
                return cluster.incr(s);
            }

            @Override
            public Long append(String s, String s1) {
                return cluster.append(s, s1);
            }

            @Override
            public String substr(String s, int i, int i1) {
                return cluster.substr(s, i, i1);
            }

            @Override
            public Long hset(String s, String s1, String s2) {
                return cluster.hset(s, s1, s2);
            }

            @Override
            public String hget(String s, String s1) {
                return cluster.hget(s, s1);
            }

            @Override
            public Long hsetnx(String s, String s1, String s2) {
                return cluster.hsetnx(s, s1, s2);
            }

            @Override
            public String hmset(String s, Map<String, String> map) {
                return cluster.hmset(s, map);
            }

            @Override
            public List<String> hmget(String s, String... strings) {
                return cluster.hmget(s, strings);
            }

            @Override
            public Long hincrBy(String s, String s1, long l) {
                return cluster.hincrBy(s, s1, l);
            }

            @Override
            public Double hincrByFloat(String s, String s1, double v) {
                return cluster.hincrByFloat(s, s1, v);
            }

            @Override
            public Boolean hexists(String s, String s1) {
                return cluster.hexists(s, s1);
            }

            @Override
            public Long hdel(String s, String... strings) {
                return cluster.hdel(s, strings);
            }

            @Override
            public Long hlen(String s) {
                return cluster.hlen(s);
            }

            @Override
            public Set<String> hkeys(String s) {
                return cluster.hkeys(s);
            }

            @Override
            public List<String> hvals(String s) {
                return cluster.hvals(s);
            }

            @Override
            public Map<String, String> hgetAll(String s) {
                return cluster.hgetAll(s);
            }

            @Override
            public Long rpush(String s, String... strings) {
                return cluster.rpush(s, strings);
            }

            @Override
            public Long lpush(String s, String... strings) {
                return cluster.lpush(s, strings);
            }

            @Override
            public Long llen(String s) {
                return cluster.llen(s);
            }

            @Override
            public List<String> lrange(String s, long l, long l1) {
                return cluster.lrange(s, l, l1);
            }

            @Override
            public String ltrim(String s, long l, long l1) {
                return cluster.ltrim(s, l, l1);
            }

            @Override
            public String lindex(String s, long l) {
                return cluster.lindex(s, l);
            }

            @Override
            public String lset(String s, long l, String s1) {
                return cluster.lset(s, l, s1);
            }

            @Override
            public Long lrem(String s, long l, String s1) {
                return cluster.lrem(s, l, s1);
            }

            @Override
            public String lpop(String s) {
                return cluster.lpop(s);
            }

            @Override
            public String rpop(String s) {
                return cluster.rpop(s);
            }

            @Override
            public Long sadd(String s, String... strings) {
                return cluster.sadd(s, strings);
            }

            @Override
            public Set<String> smembers(String s) {
                return cluster.smembers(s);
            }

            @Override
            public Long srem(String s, String... strings) {
                return cluster.srem(s, strings);
            }

            @Override
            public String spop(String s) {
                return cluster.spop(s);
            }

            @Override
            public Set<String> spop(String s, long l) {
                return cluster.spop(s, l);
            }

            @Override
            public Long scard(String s) {
                return cluster.scard(s);
            }

            @Override
            public Boolean sismember(String s, String s1) {
                return cluster.sismember(s, s1);
            }

            @Override
            public String srandmember(String s) {
                return cluster.srandmember(s);
            }

            @Override
            public List<String> srandmember(String s, int i) {
                return cluster.srandmember(s, i);
            }

            @Override
            public Long strlen(String s) {
                return cluster.strlen(s);
            }

            @Override
            public Long zadd(String s, double v, String s1) {
                return cluster.zadd(s, v, s1);
            }

            @Override
            public Long zadd(String s, double v, String s1, ZAddParams zAddParams) {
                return cluster.zadd(s, v, s1, zAddParams);
            }

            @Override
            public Long zadd(String s, Map<String, Double> map) {
                return cluster.zadd(s, map);
            }

            @Override
            public Long zadd(String s, Map<String, Double> map, ZAddParams zAddParams) {
                return cluster.zadd(s, map, zAddParams);
            }

            @Override
            public Set<String> zrange(String s, long l, long l1) {
                return cluster.zrange(s, l, l1);
            }

            @Override
            public Long zrem(String s, String... strings) {
                return cluster.zrem(s, strings);
            }

            @Override
            public Double zincrby(String s, double v, String s1) {
                return cluster.zincrby(s, v, s1);
            }

            @Override
            public Double zincrby(String s, double v, String s1, ZIncrByParams zIncrByParams) {
                return cluster.zincrby(s, v, s1, zIncrByParams);
            }

            @Override
            public Long zrank(String s, String s1) {
                return cluster.zrank(s, s1);
            }

            @Override
            public Long zrevrank(String s, String s1) {
                return cluster.zrevrank(s, s1);
            }

            @Override
            public Set<String> zrevrange(String s, long l, long l1) {
                return cluster.zrevrange(s, l, l1);
            }

            @Override
            public Set<Tuple> zrangeWithScores(String s, long l, long l1) {
                return cluster.zrangeWithScores(s, l, l1);
            }

            @Override
            public Set<Tuple> zrevrangeWithScores(String s, long l, long l1) {
                return cluster.zrevrangeWithScores(s, l, l1);
            }

            @Override
            public Long zcard(String s) {
                return cluster.zcard(s);
            }

            @Override
            public Double zscore(String s, String s1) {
                return cluster.zscore(s, s1);
            }

            @Override
            public List<String> sort(String s) {
                return cluster.sort(s);
            }

            @Override
            public List<String> sort(String s, SortingParams sortingParams) {
                return cluster.sort(s, sortingParams);
            }

            @Override
            public Long zcount(String s, double v, double v1) {
                return cluster.zcount(s, v, v1);
            }

            @Override
            public Long zcount(String s, String s1, String s2) {
                return cluster.zcount(s, s1, s2);
            }

            @Override
            public Set<String> zrangeByScore(String s, double v, double v1) {
                return cluster.zrangeByScore(s, v, v1);
            }

            @Override
            public Set<String> zrangeByScore(String s, String s1, String s2) {
                return cluster.zrangeByScore(s, s1, s2);
            }

            @Override
            public Set<String> zrevrangeByScore(String s, double v, double v1) {
                return cluster.zrevrangeByScore(s, v, v1);
            }

            @Override
            public Set<String> zrangeByScore(String s, double v, double v1, int i, int i1) {
                return cluster.zrangeByScore(s, v, v1, i, i1);
            }

            @Override
            public Set<String> zrevrangeByScore(String s, String s1, String s2) {
                return cluster.zrevrangeByScore(s, s1, s2);
            }

            @Override
            public Set<String> zrangeByScore(String s, String s1, String s2, int i, int i1) {
                return cluster.zrangeByScore(s, s1, s2, i, i1);
            }

            @Override
            public Set<String> zrevrangeByScore(String s, double v, double v1, int i, int i1) {
                return cluster.zrevrangeByScore(s, v, v1, i, i1);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(String s, double v, double v1) {
                return cluster.zrangeByScoreWithScores(s, v, v1);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(String s, double v, double v1) {
                return cluster.zrevrangeByScoreWithScores(s, v, v1);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(String s, double v, double v1, int i, int i1) {
                return cluster.zrangeByScoreWithScores(s, v, v1, i, i1);
            }

            @Override
            public Set<String> zrevrangeByScore(String s, String s1, String s2, int i, int i1) {
                return cluster.zrevrangeByScore(s, s1, s2, i, i1);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(String s, String s1, String s2) {
                return cluster.zrangeByScoreWithScores(s, s1, s2);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(String s, String s1, String s2) {
                return cluster.zrevrangeByScoreWithScores(s, s1, s2);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(String s, String s1, String s2, int i, int i1) {
                return cluster.zrangeByScoreWithScores(s, s1, s2, i, i1);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(String s, double v, double v1, int i, int i1) {
                return cluster.zrevrangeByScoreWithScores(s, v, v1, i, i1);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(String s, String s1, String s2, int i, int i1) {
                return cluster.zrevrangeByScoreWithScores(s, s1, s2, i, i1);
            }

            @Override
            public Long zremrangeByRank(String s, long l, long l1) {
                return cluster.zremrangeByRank(s, l, l1);
            }

            @Override
            public Long zremrangeByScore(String s, double v, double v1) {
                return cluster.zremrangeByScore(s, v, v1);
            }

            @Override
            public Long zremrangeByScore(String s, String s1, String s2) {
                return cluster.zremrangeByScore(s, s1, s2);
            }

            @Override
            public Long zlexcount(String s, String s1, String s2) {
                return cluster.zlexcount(s, s1, s2);
            }

            @Override
            public Set<String> zrangeByLex(String s, String s1, String s2) {
                return cluster.zrangeByLex(s, s1, s2);
            }

            @Override
            public Set<String> zrangeByLex(String s, String s1, String s2, int i, int i1) {
                return cluster.zrangeByLex(s, s1, s2, i, i1);
            }

            @Override
            public Set<String> zrevrangeByLex(String s, String s1, String s2) {
                return cluster.zrevrangeByLex(s, s1, s2);
            }

            @Override
            public Set<String> zrevrangeByLex(String s, String s1, String s2, int i, int i1) {
                return cluster.zrevrangeByLex(s, s1, s2, i, i1);
            }

            @Override
            public Long zremrangeByLex(String s, String s1, String s2) {
                return cluster.zremrangeByLex(s, s1, s2);
            }

            @Override
            public Long linsert(String s, BinaryClient.LIST_POSITION list_position, String s1, String s2) {
                return cluster.linsert(s, list_position, s1, s2);
            }

            @Override
            public Long lpushx(String s, String... strings) {
                return cluster.lpushx(s, strings);
            }

            @Override
            public Long rpushx(String s, String... strings) {
                return cluster.rpushx(s, strings);
            }

            @Override
            @Deprecated
            public List<String> blpop(String s) {
                return cluster.blpop(s);
            }

            @Override
            public List<String> blpop(int i, String s) {
                return cluster.blpop(i, s);
            }

            @Override
            @Deprecated
            public List<String> brpop(String s) {
                return cluster.brpop(s);
            }

            @Override
            public List<String> brpop(int i, String s) {
                return cluster.brpop(i, s);
            }

            @Override
            public Long del(String s) {
                return cluster.del(s);
            }

            @Override
            public String echo(String s) {
                return cluster.echo(s);
            }

            @Override
            @Deprecated
            public Long move(String s, int i) {
                return cluster.move(s, i);
            }

            @Override
            public Long bitcount(String s) {
                return cluster.bitcount(s);
            }

            @Override
            public Long bitcount(String s, long l, long l1) {
                return cluster.bitcount(s, l, l1);
            }

            @Override
            public Long bitpos(String s, boolean b) {
                return cluster.bitpos(s, b);
            }

            @Override
            public Long bitpos(String s, boolean b, BitPosParams bitPosParams) {
                return cluster.bitpos(s, b, bitPosParams);
            }

            @Override
            @Deprecated
            public ScanResult<Map.Entry<String, String>> hscan(String s, int i) {
                return cluster.hscan(s, i);
            }

            @Override
            @Deprecated
            public ScanResult<String> sscan(String s, int i) {
                return cluster.sscan(s, i);
            }

            @Override
            @Deprecated
            public ScanResult<Tuple> zscan(String s, int i) {
                return cluster.zscan(s, i);
            }

            @Override
            public ScanResult<Map.Entry<String, String>> hscan(String s, String s1) {
                return cluster.hscan(s, s1);
            }

            @Override
            public ScanResult<Map.Entry<String, String>> hscan(String s, String s1, ScanParams scanParams) {
                return cluster.hscan(s, s1, scanParams);
            }

            @Override
            public ScanResult<String> sscan(String s, String s1) {
                return cluster.sscan(s, s1);
            }

            @Override
            public ScanResult<String> sscan(String s, String s1, ScanParams scanParams) {
                return cluster.sscan(s, s1, scanParams);
            }

            @Override
            public ScanResult<Tuple> zscan(String s, String s1) {
                return cluster.zscan(s, s1);
            }

            @Override
            public ScanResult<Tuple> zscan(String s, String s1, ScanParams scanParams) {
                return cluster.zscan(s, s1, scanParams);
            }

            @Override
            public Long pfadd(String s, String... strings) {
                return cluster.pfadd(s, strings);
            }

            @Override
            public long pfcount(String s) {
                return cluster.pfcount(s);
            }

            @Override
            public Long geoadd(String s, double v, double v1, String s1) {
                return cluster.geoadd(s, v, v1, s1);
            }

            @Override
            public Long geoadd(String s, Map<String, GeoCoordinate> map) {
                return cluster.geoadd(s, map);
            }

            @Override
            public Double geodist(String s, String s1, String s2) {
                return cluster.geodist(s, s1, s2);
            }

            @Override
            public Double geodist(String s, String s1, String s2, GeoUnit geoUnit) {
                return cluster.geodist(s, s1, s2, geoUnit);
            }

            @Override
            public List<String> geohash(String s, String... strings) {
                return cluster.geohash(s, strings);
            }

            @Override
            public List<GeoCoordinate> geopos(String s, String... strings) {
                return cluster.geopos(s, strings);
            }

            @Override
            public List<GeoRadiusResponse> georadius(String s, double v, double v1, double v2, GeoUnit geoUnit) {
                return cluster.georadius(s, v, v1, v2, geoUnit);
            }

            @Override
            public List<GeoRadiusResponse> georadius(String s, double v, double v1, double v2, GeoUnit geoUnit, GeoRadiusParam geoRadiusParam) {
                return cluster.georadius(s, v, v1, v2, geoUnit, geoRadiusParam);
            }

            @Override
            public List<GeoRadiusResponse> georadiusByMember(String s, String s1, double v, GeoUnit geoUnit) {
                return cluster.georadiusByMember(s, s1, v, geoUnit);
            }

            @Override
            public List<GeoRadiusResponse> georadiusByMember(String s, String s1, double v, GeoUnit geoUnit, GeoRadiusParam geoRadiusParam) {
                return cluster.georadiusByMember(s, s1, v, geoUnit, geoRadiusParam);
            }

            @Override
            public List<Long> bitfield(String s, String... strings) {
                return cluster.bitfield(s, strings);
            }
        };
    }

    /**
     * 为了变态的 jedis 接口设计，搞了五百多行垃圾代码
     * @param cluster Jedis 集群实例
     * @return
     */
    private BinaryJedisCommands toBinaryJedisCommands(JedisCluster cluster) {
        return new BinaryJedisCommands(){
            @Override
            public String set(byte[] bytes, byte[] bytes1) {
                return cluster.set(bytes, bytes1);
            }

            @Override
            public String set(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return null;
            }

            @Override
            public String set(byte[] bytes, byte[] bytes1, byte[] bytes2, byte[] bytes3, long l) {
                return null;
            }

            @Override
            public byte[] get(byte[] bytes) {
                return cluster.get(bytes);
            }

            @Override
            public Boolean exists(byte[] bytes) {
                return cluster.exists(bytes);
            }

            @Override
            public Long persist(byte[] bytes) {
                return cluster.persist(bytes);
            }

            @Override
            public String type(byte[] bytes) {
                return cluster.type(bytes);
            }

            @Override
            public Long expire(byte[] bytes, int i) {
                return cluster.expire(bytes, i);
            }

            @Override
            public Long pexpire(String s, long l) {
                return cluster.pexpire(s, l);
            }

            @Override
            public Long pexpire(byte[] bytes, long l) {
                return cluster.pexpire(bytes, l);
            }

            @Override
            public Long expireAt(byte[] bytes, long l) {
                return cluster.expireAt(bytes, l);
            }

            @Override
            public Long pexpireAt(byte[] bytes, long l) {
                return cluster.pexpireAt(bytes, l);
            }

            @Override
            public Long ttl(byte[] bytes) {
                return cluster.ttl(bytes);
            }

            @Override
            public Boolean setbit(byte[] bytes, long l, boolean b) {
                return cluster.setbit(bytes, l, b);
            }

            @Override
            public Boolean setbit(byte[] bytes, long l, byte[] bytes1) {
                return cluster.setbit(bytes, l, bytes1);
            }

            @Override
            public Boolean getbit(byte[] bytes, long l) {
                return cluster.getbit(bytes, l);
            }

            @Override
            public Long setrange(byte[] bytes, long l, byte[] bytes1) {
                return cluster.setrange(bytes, l, bytes1);
            }

            @Override
            public byte[] getrange(byte[] bytes, long l, long l1) {
                return cluster.getrange(bytes,l,l1);
            }

            @Override
            public byte[] getSet(byte[] bytes, byte[] bytes1) {
                return cluster.getSet(bytes, bytes1);
            }

            @Override
            public Long setnx(byte[] bytes, byte[] bytes1) {
                return cluster.setnx(bytes, bytes1);
            }

            @Override
            public String setex(byte[] bytes, int i, byte[] bytes1) {
                return cluster.setex(bytes, i, bytes1);
            }

            @Override
            public Long decrBy(byte[] bytes, long l) {
                return cluster.decrBy(bytes, l);
            }

            @Override
            public Long decr(byte[] bytes) {
                return cluster.decr(bytes);
            }

            @Override
            public Long incrBy(byte[] bytes, long l) {
                return cluster.incrBy(bytes, l);
            }

            @Override
            public Double incrByFloat(byte[] bytes, double v) {
                return cluster.incrByFloat(bytes, v);
            }

            @Override
            public Long incr(byte[] bytes) {
                return cluster.incr(bytes);
            }

            @Override
            public Long append(byte[] bytes, byte[] bytes1) {
                return cluster.append(bytes, bytes1);
            }

            @Override
            public byte[] substr(byte[] bytes, int i, int i1) {
                return cluster.substr(bytes, i, i1);
            }

            @Override
            public Long hset(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.hset(bytes, bytes1, bytes2);
            }

            @Override
            public byte[] hget(byte[] bytes, byte[] bytes1) {
                return cluster.hget(bytes, bytes1);
            }

            @Override
            public Long hsetnx(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.hsetnx(bytes, bytes1, bytes2);
            }

            @Override
            public String hmset(byte[] bytes, Map<byte[], byte[]> map) {
                return cluster.hmset(bytes, map);
            }

            @Override
            public List<byte[]> hmget(byte[] bytes, byte[]... bytes1) {
                return cluster.hmget(bytes, bytes1);
            }

            @Override
            public Long hincrBy(byte[] bytes, byte[] bytes1, long l) {
                return cluster.hincrBy(bytes, bytes1, l);
            }

            @Override
            public Double hincrByFloat(byte[] bytes, byte[] bytes1, double v) {
                return cluster.hincrByFloat(bytes, bytes1, v);
            }

            @Override
            public Boolean hexists(byte[] bytes, byte[] bytes1) {
                return cluster.hexists(bytes, bytes1);
            }

            @Override
            public Long hdel(byte[] bytes, byte[]... bytes1) {
                return cluster.hdel(bytes, bytes1);
            }

            @Override
            public Long hlen(byte[] bytes) {
                return cluster.hlen(bytes);
            }

            @Override
            public Set<byte[]> hkeys(byte[] bytes) {
                return cluster.hkeys(bytes);
            }

            @Override
            public Collection<byte[]> hvals(byte[] bytes) {
                return cluster.hvals(bytes);
            }

            @Override
            public Map<byte[], byte[]> hgetAll(byte[] bytes) {
                return cluster.hgetAll(bytes);
            }

            @Override
            public Long rpush(byte[] bytes, byte[]... bytes1) {
                return cluster.rpush(bytes, bytes1);
            }

            @Override
            public Long lpush(byte[] bytes, byte[]... bytes1) {
                return cluster.lpush(bytes, bytes1);
            }

            @Override
            public Long llen(byte[] bytes) {
                return cluster.llen(bytes);
            }

            @Override
            public List<byte[]> lrange(byte[] bytes, long l, long l1) {
                return cluster.lrange(bytes, l, l1);
            }

            @Override
            public String ltrim(byte[] bytes, long l, long l1) {
                return cluster.ltrim(bytes, l, l1);
            }

            @Override
            public byte[] lindex(byte[] bytes, long l) {
                return cluster.lindex(bytes, l);
            }

            @Override
            public String lset(byte[] bytes, long l, byte[] bytes1) {
                return cluster.lset(bytes, l, bytes1);
            }

            @Override
            public Long lrem(byte[] bytes, long l, byte[] bytes1) {
                return cluster.lrem(bytes, l, bytes1);
            }

            @Override
            public byte[] lpop(byte[] bytes) {
                return cluster.lpop(bytes);
            }

            @Override
            public byte[] rpop(byte[] bytes) {
                return cluster.rpop(bytes);
            }

            @Override
            public Long sadd(byte[] bytes, byte[]... bytes1) {
                return cluster.sadd(bytes, bytes1);
            }

            @Override
            public Set<byte[]> smembers(byte[] bytes) {
                return cluster.smembers(bytes);
            }

            @Override
            public Long srem(byte[] bytes, byte[]... bytes1) {
                return cluster.srem(bytes, bytes1);
            }

            @Override
            public byte[] spop(byte[] bytes) {
                return cluster.spop(bytes);
            }

            @Override
            public Set<byte[]> spop(byte[] bytes, long l) {
                return cluster.spop(bytes, l);
            }

            @Override
            public Long scard(byte[] bytes) {
                return cluster.scard(bytes);
            }

            @Override
            public Boolean sismember(byte[] bytes, byte[] bytes1) {
                return cluster.sismember(bytes, bytes1);
            }

            @Override
            public byte[] srandmember(byte[] bytes) {
                return cluster.srandmember(bytes);
            }

            @Override
            public List<byte[]> srandmember(byte[] bytes, int i) {
                return cluster.srandmember(bytes, i);
            }

            @Override
            public Long strlen(byte[] bytes) {
                return cluster.strlen(bytes);
            }

            @Override
            public Long zadd(byte[] bytes, double v, byte[] bytes1) {
                return cluster.zadd(bytes, v, bytes1);
            }

            @Override
            public Long zadd(byte[] bytes, double v, byte[] bytes1, ZAddParams zAddParams) {
                return cluster.zadd(bytes, v, bytes1, zAddParams);
            }

            @Override
            public Long zadd(byte[] bytes, Map<byte[], Double> map) {
                return cluster.zadd(bytes, map);
            }

            @Override
            public Long zadd(byte[] bytes, Map<byte[], Double> map, ZAddParams zAddParams) {
                return cluster.zadd(bytes, map, zAddParams);
            }

            @Override
            public Set<byte[]> zrange(byte[] bytes, long l, long l1) {
                return cluster.zrange(bytes, l, l1);
            }

            @Override
            public Long zrem(byte[] bytes, byte[]... bytes1) {
                return cluster.zrem(bytes, bytes1);
            }

            @Override
            public Double zincrby(byte[] bytes, double v, byte[] bytes1) {
                return cluster.zincrby(bytes, v, bytes1);
            }

            @Override
            public Double zincrby(byte[] bytes, double v, byte[] bytes1, ZIncrByParams zIncrByParams) {
                return cluster.zincrby(bytes, v, bytes1, zIncrByParams);
            }

            @Override
            public Long zrank(byte[] bytes, byte[] bytes1) {
                return cluster.zrank(bytes, bytes1);
            }

            @Override
            public Long zrevrank(byte[] bytes, byte[] bytes1) {
                return cluster.zrevrank(bytes, bytes1);
            }

            @Override
            public Set<byte[]> zrevrange(byte[] bytes, long l, long l1) {
                return cluster.zrevrange(bytes, l, l1);
            }

            @Override
            public Set<Tuple> zrangeWithScores(byte[] bytes, long l, long l1) {
                return cluster.zrangeWithScores(bytes, l, l1);
            }

            @Override
            public Set<Tuple> zrevrangeWithScores(byte[] bytes, long l, long l1) {
                return cluster.zrevrangeWithScores(bytes, l, l1);
            }

            @Override
            public Long zcard(byte[] bytes) {
                return cluster.zcard(bytes);
            }

            @Override
            public Double zscore(byte[] bytes, byte[] bytes1) {
                return cluster.zscore(bytes, bytes1);
            }

            @Override
            public List<byte[]> sort(byte[] bytes) {
                return cluster.sort(bytes);
            }

            @Override
            public List<byte[]> sort(byte[] bytes, SortingParams sortingParams) {
                return cluster.sort(bytes, sortingParams);
            }

            @Override
            public Long zcount(byte[] bytes, double v, double v1) {
                return cluster.zcount(bytes, v, v1);
            }

            @Override
            public Long zcount(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zcount(bytes, bytes1, bytes2);
            }

            @Override
            public Set<byte[]> zrangeByScore(byte[] bytes, double v, double v1) {
                return cluster.zrangeByScore(bytes, v, v1);
            }

            @Override
            public Set<byte[]> zrangeByScore(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zrangeByScore(bytes, bytes1, bytes2);
            }

            @Override
            public Set<byte[]> zrevrangeByScore(byte[] bytes, double v, double v1) {
                return cluster.zrevrangeByScore(bytes, v, v1);
            }

            @Override
            public Set<byte[]> zrangeByScore(byte[] bytes, double v, double v1, int i, int i1) {
                return cluster.zrangeByScore(bytes, v,v1,i,i1);
            }

            @Override
            public Set<byte[]> zrevrangeByScore(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zrevrangeByScore(bytes, bytes1, bytes2);
            }

            @Override
            public Set<byte[]> zrangeByScore(byte[] bytes, byte[] bytes1, byte[] bytes2, int i, int i1) {
                return cluster.zrangeByScore(bytes, bytes1, bytes2, i,i1);
            }

            @Override
            public Set<byte[]> zrevrangeByScore(byte[] bytes, double v, double v1, int i, int i1) {
                return cluster.zrevrangeByScore(bytes, v,v1,i,i1);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(byte[] bytes, double v, double v1) {
                return cluster.zrangeByScoreWithScores(bytes,v,v1);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(byte[] bytes, double v, double v1) {
                return cluster.zrevrangeByScoreWithScores(bytes, v, v1);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(byte[] bytes, double v, double v1, int i, int i1) {
                return cluster.zrangeByScoreWithScores(bytes, v, v1, i, i1);
            }

            @Override
            public Set<byte[]> zrevrangeByScore(byte[] bytes, byte[] bytes1, byte[] bytes2, int i, int i1) {
                return cluster.zrevrangeByScore(bytes, bytes1, bytes2, i, i1);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zrangeByScoreWithScores(bytes, bytes1, bytes2);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zrevrangeByScoreWithScores(bytes, bytes1, bytes2);
            }

            @Override
            public Set<Tuple> zrangeByScoreWithScores(byte[] bytes, byte[] bytes1, byte[] bytes2, int i, int i1) {
                return cluster.zrangeByScoreWithScores(bytes, bytes1, bytes2, i, i1);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(byte[] bytes, double v, double v1, int i, int i1) {
                return cluster.zrevrangeByScoreWithScores(bytes, v, v1, i, i1);
            }

            @Override
            public Set<Tuple> zrevrangeByScoreWithScores(byte[] bytes, byte[] bytes1, byte[] bytes2, int i, int i1) {
                return cluster.zrevrangeByScoreWithScores(bytes, bytes1, bytes2, i, i1);
            }

            @Override
            public Long zremrangeByRank(byte[] bytes, long l, long l1) {
                return cluster.zremrangeByRank(bytes, l ,l1);
            }

            @Override
            public Long zremrangeByScore(byte[] bytes, double v, double v1) {
                return cluster.zremrangeByScore(bytes, v, v1);
            }

            @Override
            public Long zremrangeByScore(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zremrangeByScore(bytes, bytes1, bytes2);
            }

            @Override
            public Long zlexcount(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zlexcount(bytes, bytes1, bytes2);
            }

            @Override
            public Set<byte[]> zrangeByLex(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zrangeByLex(bytes, bytes1, bytes2);
            }

            @Override
            public Set<byte[]> zrangeByLex(byte[] bytes, byte[] bytes1, byte[] bytes2, int i, int i1) {
                return cluster.zrangeByLex(bytes, bytes1, bytes2, i, i1);
            }

            @Override
            public Set<byte[]> zrevrangeByLex(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zrevrangeByLex(bytes, bytes1, bytes2);
            }

            @Override
            public Set<byte[]> zrevrangeByLex(byte[] bytes, byte[] bytes1, byte[] bytes2, int i, int i1) {
                return cluster.zrevrangeByLex(bytes, bytes1, bytes2, i, i1);
            }

            @Override
            public Long zremrangeByLex(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.zremrangeByLex(bytes, bytes1, bytes2);
            }

            @Override
            public Long linsert(byte[] bytes, BinaryClient.LIST_POSITION list_position, byte[] bytes1, byte[] bytes2) {
                return cluster.linsert(bytes, list_position, bytes1, bytes2);
            }

            @Override
            public Long lpushx(byte[] bytes, byte[]... bytes1) {
                return cluster.lpushx(bytes, bytes1);
            }

            @Override
            public Long rpushx(byte[] bytes, byte[]... bytes1) {
                return cluster.rpushx(bytes, bytes1);
            }

            @Override
            public List<byte[]> blpop(byte[] bytes) {
                return cluster.blpop(0, bytes);
            }

            @Override
            public List<byte[]> brpop(byte[] bytes) {
                return cluster.brpop(0, bytes);
            }

            @Override
            public Long del(byte[] bytes) {
                return cluster.del(bytes);
            }

            @Override
            public byte[] echo(byte[] bytes) {
                return cluster.echo(bytes);
            }

            @Override
            public Long move(byte[] bytes, int i) {
                return cluster.move(new String(bytes), i);
            }

            @Override
            public Long bitcount(byte[] bytes) {
                return cluster.bitcount(bytes);
            }

            @Override
            public Long bitcount(byte[] bytes, long l, long l1) {
                return cluster.bitcount(bytes, l, l1);
            }

            @Override
            public Long pfadd(byte[] bytes, byte[]... bytes1) {
                return cluster.pfadd(bytes, bytes1);
            }

            @Override
            public long pfcount(byte[] bytes) {
                return cluster.pfcount(bytes);
            }

            @Override
            public Long geoadd(byte[] bytes, double v, double v1, byte[] bytes1) {
                return cluster.geoadd(bytes, v, v1, bytes1);
            }

            @Override
            public Long geoadd(byte[] bytes, Map<byte[], GeoCoordinate> map) {
                return cluster.geoadd(bytes, map);
            }

            @Override
            public Double geodist(byte[] bytes, byte[] bytes1, byte[] bytes2) {
                return cluster.geodist(bytes, bytes1, bytes2);
            }

            @Override
            public Double geodist(byte[] bytes, byte[] bytes1, byte[] bytes2, GeoUnit geoUnit) {
                return cluster.geodist(bytes, bytes1, bytes2, geoUnit);
            }

            @Override
            public List<byte[]> geohash(byte[] bytes, byte[]... bytes1) {
                return cluster.geohash(bytes, bytes1);
            }

            @Override
            public List<GeoCoordinate> geopos(byte[] bytes, byte[]... bytes1) {
                return cluster.geopos(bytes, bytes1);
            }

            @Override
            public List<GeoRadiusResponse> georadius(byte[] bytes, double v, double v1, double v2, GeoUnit geoUnit) {
                return cluster.georadius(bytes, v,v1,v2, geoUnit);
            }

            @Override
            public List<GeoRadiusResponse> georadius(byte[] bytes, double v, double v1, double v2, GeoUnit geoUnit, GeoRadiusParam geoRadiusParam) {
                return cluster.georadius(bytes, v, v1, v2, geoUnit, geoRadiusParam);
            }

            @Override
            public List<GeoRadiusResponse> georadiusByMember(byte[] bytes, byte[] bytes1, double v, GeoUnit geoUnit) {
                return cluster.georadiusByMember(bytes, bytes1, v, geoUnit);
            }

            @Override
            public List<GeoRadiusResponse> georadiusByMember(byte[] bytes, byte[] bytes1, double v, GeoUnit geoUnit, GeoRadiusParam geoRadiusParam) {
                return cluster.georadiusByMember(bytes, bytes1, v, geoUnit, geoRadiusParam);
            }

            @Override
            public ScanResult<Map.Entry<byte[], byte[]>> hscan(byte[] bytes, byte[] bytes1) {
                return cluster.hscan(bytes, bytes1);
            }

            @Override
            public ScanResult<Map.Entry<byte[], byte[]>> hscan(byte[] bytes, byte[] bytes1, ScanParams scanParams) {
                return cluster.hscan(bytes, bytes1, scanParams);
            }

            @Override
            public ScanResult<byte[]> sscan(byte[] bytes, byte[] bytes1) {
                return cluster.sscan(bytes, bytes1);
            }

            @Override
            public ScanResult<byte[]> sscan(byte[] bytes, byte[] bytes1, ScanParams scanParams) {
                return cluster.sscan(bytes, bytes1, scanParams);
            }

            @Override
            public ScanResult<Tuple> zscan(byte[] bytes, byte[] bytes1) {
                return cluster.zscan(bytes, bytes1);
            }

            @Override
            public ScanResult<Tuple> zscan(byte[] bytes, byte[] bytes1, ScanParams scanParams) {
                return cluster.zscan(bytes, bytes1, scanParams);
            }

            @Override
            public List<byte[]> bitfield(byte[] bytes, byte[]... bytes1) {
                return cluster.bitfield(bytes, bytes1);
            }
        };
    }

}
