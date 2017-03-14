package net.oschina.j2cache.redis.support;

import net.oschina.j2cache.redis.client.RedisClient;

import java.io.Closeable;

/**
 * @author zhangyw
 */
public interface RedisClientFactory<C extends RedisClient> extends Closeable {

    void build();

    /**
     * 在 大并发情况下，实现类的getResource 方法，
     * 务必 加上 synchronized 关键子，
     * 保证 resource 的一致性
     *
     * @return
     */
    C getResource();

    void returnResource(C client);

}
