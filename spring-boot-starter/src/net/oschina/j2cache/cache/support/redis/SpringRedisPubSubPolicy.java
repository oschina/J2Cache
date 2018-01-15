package net.oschina.j2cache.cache.support.redis;

import java.io.Serializable;
import java.util.Properties;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import net.oschina.j2cache.ClusterPolicy;
import net.oschina.j2cache.Command;
import net.oschina.j2cache.cache.support.util.SpringUtil;

/**
 * 
 * @author zhangsaizz
 *
 */
public class SpringRedisPubSubPolicy implements ClusterPolicy{
	
	private RedisConnectionFactory redisConnectionFactory;
	
	private RedisTemplate<String, Serializable> redisTemplate;
	
	private String channel = "j2cache_channel";

	@SuppressWarnings("unchecked")
	@Override
	public void connect(Properties props) {
		String channel_name = props.getProperty("jgroups.channel.name");
		if(channel_name == null || channel_name.isEmpty()) {
			this.channel = channel_name;
		}
		this.redisConnectionFactory = SpringUtil.getBean(RedisConnectionFactory.class);
		this.redisTemplate = SpringUtil.getBean("j2CacheRedisTemplate", RedisTemplate.class);
		RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
		listenerContainer.setConnectionFactory(this.redisConnectionFactory);
		listenerContainer.addMessageListener(new SpringRedisMessageListener(this, this.channel), new PatternTopic(this.channel));
	}

	@Override
	public void sendEvictCmd(String region, String... keys) {
        Command cmd = new Command(Command.OPT_EVICT_KEY, region, keys);
        redisTemplate.convertAndSend(this.channel, cmd.jsonBytes());	
	}

	@Override
	public void sendClearCmd(String region) {
        Command cmd = new Command(Command.OPT_CLEAR_KEY, region, "");
		redisTemplate.convertAndSend(this.channel, cmd.jsonBytes());	
	}

	@Override
	public void disconnect() {
		redisTemplate.convertAndSend(this.channel, Command.quit().jsonBytes());
	}

	
}
