package net.oschina.j2cache.redis;

import net.oschina.j2cache.CacheException;
import redis.clients.jedis.*;
import redis.clients.jedis.params.geo.GeoRadiusParam;
import redis.clients.jedis.params.sortedset.ZAddParams;
import redis.clients.jedis.params.sortedset.ZIncrByParams;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

/**
 * 封装各种模式的 Redis 客户端成统一接口
 * Jedis 接口设计真操蛋
 * @author winterlau
 */
public class RedisClient implements Closeable {

    private JedisCluster cluster;
    private JedisPool single;
    private JedisSentinelPool sentinel;
    private ShardedJedis sharded;
    private String redisPassword;

    /**
     * 各种模式 Redis 客户端的封装
     * @param mode
     * @param hosts
     * @parma password
     * @param cluster_name
     * @param poolConfig
     */
    public RedisClient(String mode, String hosts, String password, String cluster_name, JedisPoolConfig poolConfig) {
        this.redisPassword = (password != null && password.trim().length() > 0)? password.trim(): null;
        switch(mode){
            case "single":
                for(String node : hosts.split(",")) {
                    String[] infos = node.split(":");
                    String host = infos[0];
                    int port = (infos.length > 1)?Integer.parseInt(infos[1]):6379;
                    this.single = new JedisPool(poolConfig, host, port);
                    break;
                }
                break;
            case "sentinel":
                Set<String> nodes = new HashSet<>();
                for(String node : hosts.split(","))
                    nodes.add(node);
                this.sentinel = new JedisSentinelPool(cluster_name, nodes, poolConfig);
                break;
            case "cluster":
                Set<HostAndPort> hps = new HashSet<>();
                for(String node : hosts.split(",")){
                    String[] infos = node.split(":");
                    String host = infos[0];
                    int port = (infos.length > 1)?Integer.parseInt(infos[1]):6379;
                    hps.add(new HostAndPort(host, port));
                }
                this.cluster = new JedisCluster(hps, poolConfig);
                if(redisPassword != null)
                    this.cluster.auth(redisPassword);
                break;
            case "sharded":
                List<JedisShardInfo> shards = new ArrayList<>();
                for(String node : hosts.split(",")){
                    String[] infos = node.split(":");
                    String host = infos[0];
                    int port = (infos.length > 1)?Integer.parseInt(infos[1]):6379;
                    shards.add(new JedisShardInfo(host, port));
                }
                this.sharded = new ShardedJedis(shards);
                if(redisPassword != null)
                    this.sharded.getAllShards().forEach(node -> node.auth(redisPassword));
                break;
            default:
                throw new CacheException("Redis mode [" + mode + "] not defined.");
        }
    }

    /**
     * 获取客户端接口
     */
    public BinaryJedisCommands get() {
        if(single != null) {
            Jedis jedis = single.getResource();
            if(redisPassword != null)
                jedis.auth(redisPassword);
            return jedis;
        }
        else
        if(sentinel != null) {
            Jedis jedis = sentinel.getResource();
            if(redisPassword != null)
                jedis.auth(redisPassword);
            return jedis;
        }
        else
        if(sharded != null)
            return sharded;
        else
        if(cluster != null)
            return toBinaryJedisCommands(cluster);
        return null;
    }

    /**
     * 订阅
     * @param jedisPubSub
     * @param channels
     */
    public void subscribe(BinaryJedisPubSub jedisPubSub, String... channels) {
        byte[][] bytes = new byte[channels.length][];
        for(int i=0; i<channels.length; i++)
            bytes[i] = channels[i].getBytes();

        if(cluster != null)
            cluster.subscribe(jedisPubSub, bytes);
        else if(single != null)
            single.getResource().subscribe(jedisPubSub, bytes);
        else if(sentinel != null)
            sentinel.getResource().subscribe(jedisPubSub, bytes);
        if(sharded != null)
            sharded.getAllShards().forEach(node -> node.subscribe(jedisPubSub, bytes));
    }

    public void publish(byte[] channel, byte[] bytes) {

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

    /**
     * 为了变态的 jedis 接口设计，搞了五百多行垃圾代码
     * @param cluster
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
