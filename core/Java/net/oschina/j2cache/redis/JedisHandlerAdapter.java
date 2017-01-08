package net.oschina.j2cache.redis;

/**
 * jedis 适配器，自动适应平台的redis部署模式
 *
 * @author zhangyw
 * @date 16/11/25 12:11
 */
public class JedisHandlerAdapter {

    private RedisPoolConfig poolConfig;
    private AbstractJedisTemplate jedisTemplate;

    private RedisPolicy policy = RedisPolicy.single; // 缓存策略，single:单机,sharded:分片,cluster:集群

    private int cacheDefaultExpire = 1000;

    public JedisHandlerAdapter(RedisPoolConfig poolConfig, RedisPolicy policy) {
        this.policy = policy;
        this.poolConfig = poolConfig;
        initJedis();
    }

    private void initJedis() {
        switch (getPolicy()) {
            case single:
                initSingleJedisPool();
                break;
            case sharded:
                initShardedJedisPool();
                break;
            case cluster:
                initClusterJedisPool();
                break;
            default:
                initSingleJedisPool();
        }
    }

    private void initSingleJedisPool() {
        JedisSinglePoolFactory jedisSinglePoolFactory = new JedisSinglePoolFactory();
        jedisSinglePoolFactory.setPoolConfig(this.poolConfig);
        jedisSinglePoolFactory.setCacheDefaultExpire(cacheDefaultExpire);
        jedisSinglePoolFactory.build();
        this.setJedisPoolFactory(jedisSinglePoolFactory);
    }

    private void initShardedJedisPool() {
        ShardedJedisPoolFactory shardedJedisPoolFactory = new ShardedJedisPoolFactory();
        shardedJedisPoolFactory.setPoolConfig(this.poolConfig);
        shardedJedisPoolFactory.setCacheDefaultExpire(cacheDefaultExpire);
        shardedJedisPoolFactory.build();
        this.setJedisPoolFactory(shardedJedisPoolFactory);
    }

    private void initClusterJedisPool() {
        JedisClusterPoolFactory jedisClusterPoolFactory = new JedisClusterPoolFactory();
        jedisClusterPoolFactory.setPoolConfig(this.poolConfig);
        jedisClusterPoolFactory.setCacheDefaultExpire(cacheDefaultExpire);
        jedisClusterPoolFactory.build();
        this.setJedisPoolFactory(jedisClusterPoolFactory);
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

    public void setCacheDefaultExpire(int cacheDefaultExpire) {
        this.cacheDefaultExpire = cacheDefaultExpire;
    }

    public RedisPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = RedisPolicy.format(policy);
    }

    public void setJedisPoolFactory(PoolFactory poolFactory) {
        this.setJedisTemplate(poolFactory);
    }

    public void setJedisTemplate(PoolFactory poolFactory) {
        if (poolFactory instanceof ShardedJedisPoolFactory) {
            this.setJedisTemplate(new ShardedJedisTemplate((ShardedJedisPoolFactory) poolFactory));
        } else if (poolFactory instanceof JedisSinglePoolFactory) {
            this.setJedisTemplate(new JedisSingleTemplate((JedisSinglePoolFactory) poolFactory));
        } else if (poolFactory instanceof JedisClusterPoolFactory) {
            this.setJedisTemplate(new JedisClusterTemplate((JedisClusterPoolFactory) poolFactory));
        } else {
            throw new RuntimeException("unknowns pool factory");
        }
    }

    public void setJedisTemplate(AbstractJedisTemplate jedisTemplate) {
        this.jedisTemplate = jedisTemplate;
    }

    public AbstractJedisTemplate getJedisTemplate() {
        return jedisTemplate;
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
