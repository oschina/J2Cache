package net.oschina.j2cache.autoconfigure;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@AutoConfigureAfter({J2CacheAutoConfiguration.class, RedisAutoConfiguration.class})
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
}
