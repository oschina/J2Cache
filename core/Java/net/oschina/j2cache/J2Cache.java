package net.oschina.j2cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 缓存入口
 * @author winterlau
 */
public class J2Cache {

	private final static Logger log = LoggerFactory.getLogger(J2Cache.class);

	private final static String CONFIG_FILE = "/j2cache.properties";
	private final static CacheChannel channel;
	private final static Properties config;

	static {
		try {
			config = loadConfig();
			String cache_broadcast = config.getProperty("cache.broadcast");
			if ("redis".equalsIgnoreCase(cache_broadcast))
				channel = RedisCacheChannel.getInstance();
			else if ("jgroups".equalsIgnoreCase(cache_broadcast))
				channel = JGroupsCacheChannel.getInstance();
			else
				throw new CacheException("Cache Channel not defined. name = " + cache_broadcast);
		} catch (IOException e) {
			throw new CacheException("Unabled to load j2cache configuration " + CONFIG_FILE, e);
		}
	}

	public static CacheChannel getChannel(){
		return channel;
	}

	public static Properties getConfig(){
		return config;
	}

	/**
	 * 加载配置
	 * @return
	 * @throws IOException
	 */
	private static Properties loadConfig() throws IOException {
		log.info("Load J2Cache Config File : [{}].", CONFIG_FILE);
		InputStream configStream = J2Cache.class.getClassLoader().getParent().getResourceAsStream(CONFIG_FILE);
		if(configStream == null)
			configStream = CacheManager.class.getResourceAsStream(CONFIG_FILE);
		if(configStream == null)
			throw new CacheException("Cannot find " + CONFIG_FILE + " !!!");

		Properties props = new Properties();

		try{
			props.load(configStream);
		}finally{
			configStream.close();
		}

		return props;
	}

}
