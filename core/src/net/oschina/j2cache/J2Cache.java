package net.oschina.j2cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * J2Cache 的缓存入口
 * @author winterlau
 */
public class J2Cache {

	private final static Logger log = LoggerFactory.getLogger(J2Cache.class);

	private final static String CONFIG_FILE = "/j2cache.properties";

	private static CacheChannel channel;
	private static ClusterPolicy policy;

	private static String serializer;

	static {
		try {
			initFromConfig();
			/* 初始化缓存接口 */
			channel = new CacheChannel(){
				@Override
				public void sendClearCmd(String region) {
					policy.sendClearCmd(region);
				}

				@Override
				public void sendEvictCmd(String region, Serializable key) {
					policy.sendEvictCmd(region, key);
				}

				@Override
				public void close() {
					policy.disconnect();
					CacheProviderHolder.shutdown();
				}
			};
		} catch (IOException e) {
			throw new CacheException("Unable to load j2cache configuration " + CONFIG_FILE, e);
		}
	}

	public static CacheChannel getChannel(){
		return channel;
	}

	public static String getSerializer() {
		return serializer;
	}

	public static void main(String[] args) throws IOException {
		try(CacheChannel channel = J2Cache.getChannel()){
			//channel.set("Users",1, "Winter Lau");
			System.out.println(channel.getRawObject("Users", 1));
		}
	}

	/**
	 * 加载配置
	 * @return
	 * @throws IOException
	 */
	private static void initFromConfig() throws IOException {
		try(InputStream configStream = getConfigStream()){
			Properties props = new Properties();
			props.load(configStream);
			serializer = props.getProperty("j2cache.serialization");
			//初始化两级的缓存管理
			CacheProviderHolder.initCacheProvider(props, (region, key)->{
				if(policy != null)
					policy.sendEvictCmd(region, key);
			});

			String cache_broadcast = props.getProperty("j2cache.broadcast");
			if ("redis".equalsIgnoreCase(cache_broadcast)) {
				String channel = props.getProperty("redis.channel");
				policy = ClusterPolicyFactory.redis(channel, CacheProviderHolder.getRedisClient());//.getInstance();
			}
			else if ("jgroups".equalsIgnoreCase(cache_broadcast)) {
				String channel_name = props.getProperty("jgroups.channel.name");
				policy = ClusterPolicyFactory.jgroups(channel_name);//
			}
			else
				throw new CacheException("Cache Channel not defined. name = " + cache_broadcast);

		}
	}

	/**
	 * get j2cache properties stream
	 * @return
	 */
	private static InputStream getConfigStream() {
		log.info("Load J2Cache Config File : [{}].", CONFIG_FILE);
		InputStream configStream = J2Cache.class.getClassLoader().getParent().getResourceAsStream(CONFIG_FILE);
		if(configStream == null)
			configStream = J2Cache.class.getResourceAsStream(CONFIG_FILE);
		if(configStream == null)
			throw new CacheException("Cannot find " + CONFIG_FILE + " !!!");
		return configStream;
	}

}
