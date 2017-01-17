package net.oschina.j2cache.redis.support;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @author zhangyw
 * @version 17/1/8 14:56
 */
public class RedisPoolConfig extends GenericObjectPoolConfig {

    private String host;
    private int port;
    private String password;
    
    private int database;

	private int timeout = Protocol.DEFAULT_TIMEOUT;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    
    public int getDatabase() {
		return database;
	}

	public void setDatabase(int database) {
		this.database = database;
	}
	
}
