package net.oschina.j2cache.autoconfigure;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 
 * @author zhangsaizz
 *
 */
@Configuration
@AutoConfigureAfter({ J2CacheAutoConfiguration.class, RedisAutoConfiguration.class })
public class J2CacheSpringRedisAutoConfiguration {

	private RedisSerializer<Object> defaultRedisSerializer;

	@Bean("j2CacheRedisTemplate")
	@ConditionalOnBean(RedisConnectionFactory.class)
	public RedisTemplate<String, Serializable> j2CacheRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Serializable> template = new RedisTemplate<String, Serializable>();
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		if (this.defaultRedisSerializer != null) {
			template.setDefaultSerializer(this.defaultRedisSerializer);
		}
		template.setConnectionFactory(connectionFactory);
		return template;
	}

	@Autowired(required = false)
	@Qualifier("j2CacheDefaultRedisSerializer")
	public void setDefaultRedisSerializer(RedisSerializer<Object> defaultRedisSerializer) {
		this.defaultRedisSerializer = defaultRedisSerializer;
	}

//	@Bean
//	public RedisMessageListenerContainer J2CacheSpringMessageListenerContainer(
//			RedisConnectionFactory connectionFactory) {
//		RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
//		listenerContainer.setConnectionFactory(connectionFactory);
//		listenerContainer.addMessageListener(new SpringRedisMessageListener(),
//				Arrays.asList(new PatternTopic("__keyevent@*:del"), new PatternTopic("__keyevent@*:expired")));
//		return listenerContainer;
//	}

//	@Bean
//	public InitializingBean enableRedisKeyspaceNotificationsInitializer(
//			RedisConnectionFactory connectionFactory) {
//		return new EnableRedisKeyspaceNotificationsInitializer(connectionFactory);
//	}
	
	/**
	 * Ensures that Redis is configured to send keyspace notifications. This is
	 * important to ensure that expiration and deletion of sessions trigger
	 * SessionDestroyedEvents. Without the SessionDestroyedEvent resources may not
	 * get cleaned up properly. For example, the mapping of the Session to WebSocket
	 * connections may not get cleaned up.
	 */
	static class EnableRedisKeyspaceNotificationsInitializer implements InitializingBean {
		private final RedisConnectionFactory connectionFactory;
		static final String CONFIG_NOTIFY_KEYSPACE_EVENTS = "notify-keyspace-events";

		EnableRedisKeyspaceNotificationsInitializer(RedisConnectionFactory connectionFactory) {
			this.connectionFactory = connectionFactory;
		}

		public void afterPropertiesSet() throws Exception {
			RedisConnection connection = this.connectionFactory.getConnection();
			try {
				configure(connection);
			} finally {
				try {
					connection.close();
				} catch (Exception e) {

				}
			}
		}

		public void configure(RedisConnection connection) {
			String notifyOptions = getNotifyOptions(connection);
			String customizedNotifyOptions = notifyOptions;
			if (!customizedNotifyOptions.contains("E")) {
				customizedNotifyOptions += "E";
			}
			boolean A = customizedNotifyOptions.contains("A");
			if (!(A || customizedNotifyOptions.contains("g"))) {
				customizedNotifyOptions += "g";
			}
			if (!(A || customizedNotifyOptions.contains("x"))) {
				customizedNotifyOptions += "x";
			}
			if (!notifyOptions.equals(customizedNotifyOptions)) {
				connection.setConfig(CONFIG_NOTIFY_KEYSPACE_EVENTS, customizedNotifyOptions);
			}
		}

		private String getNotifyOptions(RedisConnection connection) {
			try {
				List<String> config = connection.getConfig(CONFIG_NOTIFY_KEYSPACE_EVENTS);
				if (config.size() < 2) {
					return "";
				}
				return config.get(1);
			} catch (InvalidDataAccessApiUsageException e) {
				throw new IllegalStateException(
						"Unable to configure Redis to keyspace notifications. See http://docs.spring.io/spring-session/docs/current/reference/html5/#api-redisoperationssessionrepository-sessiondestroyedevent",
						e);
			}
		}
	}
}
