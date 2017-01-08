package net.oschina.j2cache.redis;

import redis.clients.jedis.JedisCommands;

import java.io.Closeable;

/**
 * @author zhangyw
 * @ClassName PoolFactory
 * @Description
 * @date 16/11/25 09:42
 */
public interface PoolFactory<C extends JedisCommands> extends Closeable {

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
