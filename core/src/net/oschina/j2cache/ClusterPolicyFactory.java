package net.oschina.j2cache;

import net.oschina.j2cache.redis.RedisClient;
import net.oschina.j2cache.redis.RedisPubSubClusterPolicy;
import redis.clients.jedis.JedisCluster;

/**
 * 集群策略工厂
 * @author Winter Lau(javayou@gmail.com)
 */
public class ClusterPolicyFactory {

    /**
     * 使用 Redis 订阅和发布机制，该方法只能调用一次
     * @param name  频道名称
     * @param redis Redis 客户端接口
     * @return 返回 Redis 集群策略的实例
     */
    public final static ClusterPolicy redis(String name, RedisClient redis) {
        RedisPubSubClusterPolicy policy = new RedisPubSubClusterPolicy(name, redis);
        policy.connect();
        return policy;
    }

    /**
     * 使用 JGroups 组播机制
     * @param name  组播识别码
     * @return 返回 JGroups 集群策略的实例
     */
    public final static ClusterPolicy jgroups(String name) {
        JGroupsClusterPolicy policy = new JGroupsClusterPolicy(name);
        policy.connect();
        return policy;
    }

}
