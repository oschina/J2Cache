package net.oschina.j2cache;

import net.oschina.j2cache.redis.RedisClient;
import net.oschina.j2cache.redis.RedisPubSubClusterPolicy;
import redis.clients.jedis.JedisCluster;

/**
 * 集群策略工厂
 * @author winterlau
 */
public class ClusterPolicyFactory {

    /**
     * 使用 Redis 订阅和发布机制，该方法只能调用一次
     * @param name
     * @param redis
     * @return
     */
    public final static ClusterPolicy redis(String name, RedisClient redis) {
        RedisPubSubClusterPolicy policy = new RedisPubSubClusterPolicy(name, redis);
        policy.connect();
        return policy;
    }

    /**
     * 使用 JGroups 组播机制
     * @param name
     * @return
     */
    public final static ClusterPolicy jgroups(String name) {
        JGroupsClusterPolicy policy = new JGroupsClusterPolicy(name);
        policy.connect();
        return policy;
    }

}
