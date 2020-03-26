package com.appleframework.id.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

import com.appleframework.id.RedisIdGenerator;
import com.appleframework.id.SerialIdGenerator;

@Configuration
public class SpringRedisTemplateConfiguration {

	@Bean
	public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Long> redisTemplate = new RedisTemplate<String, Long>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		GenericToStringSerializer<Long> genericToStringSerializer = new GenericToStringSerializer<Long>(Long.class);
		redisTemplate.setValueSerializer(genericToStringSerializer);
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
	@Bean
	public SerialIdGenerator serialIdGenerator(RedisTemplate<String, Long> redisTemplate) {
        return RedisIdGenerator.getInstance(redisTemplate);
	}
	
}