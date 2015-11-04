package net.oschina.j2cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 缓存入口
 * @author winterlau
 */
public class J2Cache {

	private final static String CONFIG_FILE = "/j2cache.properties";
	private final static CacheChannel channel;
	
	static {
		try {
			Properties props = loadConfig();
			String cache_broadcast = props.getProperty("cache.broadcast");
			if("redis".equalsIgnoreCase(cache_broadcast))
				channel =  RedisCacheChannel.getInstance();
			else if("jgroups".equalsIgnoreCase(cache_broadcast))
				channel = JGroupsCacheChannel.getInstance();
			else
				throw new CacheException("Cache Channel not defined. name = " + cache_broadcast);
		} catch (IOException e) {
			throw new CacheException("Unabled to load j2cache configuration "+CONFIG_FILE, e);
		}
	}
	
	public static CacheChannel getChannel(){
		return channel;
	}

	/**
	 * 加载配置
	 * @return
	 * @throws IOException
	 */
	static Properties loadConfig() throws IOException {
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
