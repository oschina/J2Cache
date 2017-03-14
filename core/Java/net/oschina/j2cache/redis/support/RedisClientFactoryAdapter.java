package net.oschina.j2cache.redis.support;

import java.io.IOException;

/**
 * jedis 适配器，自动适应平台的redis部署模式
 *
 * @author zhangyw
 */
public class RedisClientFactoryAdapter {

    private RedisClientFactory redisClientFactory;

    private RedisPoolConfig poolConfig;
    private RedisPolicy policy = RedisPolicy.single; // 缓存策略，single:单机,sharded:分片,cluster:集群

    public RedisClientFactoryAdapter(RedisPoolConfig poolConfig, RedisPolicy policy) {
        this.policy = policy;
        this.poolConfig = poolConfig;
        initRedisFactory();
    }

    private void initRedisFactory() {
        switch (getPolicy()) {
            case single:
                initSingleRedis();
                break;
            case sharded:
                initShardedRedis();
                break;
            case cluster:
                initClusterRedis();
                break;
            default:
                initSingleRedis();
        }
    }

    private void initSingleRedis() {
        RedisSingleFactory redisSingleFactory = new RedisSingleFactory();
        redisSingleFactory.setPoolConfig(this.poolConfig);
        redisSingleFactory.build();
        this.setRedisClientFactory(redisSingleFactory);
    }

    private void initShardedRedis() {
        RedisShardedFactory redisShardedFactory = new RedisShardedFactory();
        redisShardedFactory.setPoolConfig(this.poolConfig);
        redisShardedFactory.build();
        this.setRedisClientFactory(redisShardedFactory);
    }

    private void initClusterRedis() {
        RedisClusterFactory redisClusterFactory = new RedisClusterFactory();
        redisClusterFactory.setPoolConfig(this.poolConfig);
        redisClusterFactory.build();
        this.setRedisClientFactory(redisClusterFactory);
    }

    public void setHost(String host) {
        this.poolConfig.setHost(host);
    }

    public void setPort(int port) {
        this.poolConfig.setPort(port);
    }

    public void setPassword(String password) {
        this.poolConfig.setPassword(password);
    }

    public void setTimeout(int timeout) {
        this.poolConfig.setTimeout(timeout);
    }

    public int getMaxTotal() {
        return poolConfig.getMaxTotal();
    }

    public void setMaxTotal(int maxTotal) {
        poolConfig.setMaxTotal(maxTotal);
    }

    public int getMaxIdle() {
        return poolConfig.getMaxIdle();
    }

    public void setMaxIdle(int maxIdle) {
        poolConfig.setMaxIdle(maxIdle);
    }

    public int getMinIdle() {
        return poolConfig.getMinIdle();
    }

    public void setMinIdle(int minIdle) {
        poolConfig.setMinIdle(minIdle);
    }

    public boolean getLifo() {
        return poolConfig.getLifo();
    }

    public boolean getFairness() {
        return poolConfig.getFairness();
    }

    public void setLifo(boolean lifo) {
        poolConfig.setLifo(lifo);
    }

    public void setFairness(boolean fairness) {
        poolConfig.setFairness(fairness);
    }

    public long getMaxWaitMillis() {
        return poolConfig.getMaxWaitMillis();
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        poolConfig.setMaxWaitMillis(maxWaitMillis);
    }

    public long getMinEvictableIdleTimeMillis() {
        return poolConfig.getMinEvictableIdleTimeMillis();
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        poolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    public long getSoftMinEvictableIdleTimeMillis() {
        return poolConfig.getSoftMinEvictableIdleTimeMillis();
    }

    public void setSoftMinEvictableIdleTimeMillis(long softMinEvictableIdleTimeMillis) {
        poolConfig.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
    }

    public int getNumTestsPerEvictionRun() {
        return poolConfig.getNumTestsPerEvictionRun();
    }

    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        poolConfig.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    public boolean getTestOnCreate() {
        return poolConfig.getTestOnCreate();
    }

    public void setTestOnCreate(boolean testOnCreate) {
        poolConfig.setTestOnCreate(testOnCreate);
    }

    public boolean getTestOnBorrow() {
        return poolConfig.getTestOnBorrow();
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        poolConfig.setTestOnBorrow(testOnBorrow);
    }

    public boolean getTestOnReturn() {
        return poolConfig.getTestOnReturn();
    }

    public void setTestOnReturn(boolean testOnReturn) {
        poolConfig.setTestOnReturn(testOnReturn);
    }

    public boolean getTestWhileIdle() {
        return poolConfig.getTestWhileIdle();
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        poolConfig.setTestWhileIdle(testWhileIdle);
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return poolConfig.getTimeBetweenEvictionRunsMillis();
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    public String getEvictionPolicyClassName() {
        return poolConfig.getEvictionPolicyClassName();
    }

    public void setEvictionPolicyClassName(String evictionPolicyClassName) {
        poolConfig.setEvictionPolicyClassName(evictionPolicyClassName);
    }

    public boolean getBlockWhenExhausted() {
        return poolConfig.getBlockWhenExhausted();
    }

    public void setBlockWhenExhausted(boolean blockWhenExhausted) {
        poolConfig.setBlockWhenExhausted(blockWhenExhausted);
    }

    public boolean getJmxEnabled() {
        return poolConfig.getJmxEnabled();
    }

    public void setJmxEnabled(boolean jmxEnabled) {
        poolConfig.setJmxEnabled(jmxEnabled);
    }

    public String getJmxNameBase() {
        return poolConfig.getJmxNameBase();
    }

    public void setJmxNameBase(String jmxNameBase) {
        poolConfig.setJmxNameBase(jmxNameBase);
    }

    public String getJmxNamePrefix() {
        return poolConfig.getJmxNamePrefix();
    }

    public void setJmxNamePrefix(String jmxNamePrefix) {
        poolConfig.setJmxNamePrefix(jmxNamePrefix);
    }

    public RedisPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = RedisPolicy.format(policy);
    }

    public void setRedisClientFactory(RedisClientFactory redisClientFactory) {
        this.redisClientFactory = redisClientFactory;
    }

    public RedisClientFactory getRedisClientFactory() {
        return redisClientFactory;
    }

    public void close() {
        try {
            getRedisClientFactory().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * redis使用策略
     */
    public enum RedisPolicy {
        single, // 单机
        sharded,// 分片
        cluster;// 集群


        RedisPolicy() {
        }

        public static RedisPolicy format(String policy) {
            switch (policy) {
                case "single":
                    return single;
                case "sharded":
                    return sharded;
                case "cluster":
                    return cluster;
                default:
                    return single;
            }
        }
    }
}
