package messenger.proj.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import messenger.proj.DTO.ConnectionInfoDTO;
import messenger.proj.models.Message;
import messenger.proj.models.User;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory("localhost", 6379); // Измените хост и порт на соответствующие параметры
																// вашего Redis сервера
	}

	@Bean
	public RedisTemplate<String, Message> redisTemplate() {
		RedisTemplate<String, Message> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());

		Jackson2JsonRedisSerializer<Message> jsonSerializer = new Jackson2JsonRedisSerializer<>(Message.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		jsonSerializer.setObjectMapper(objectMapper);

		template.setValueSerializer(jsonSerializer);
		template.afterPropertiesSet();

		return template;
	}

	@Bean
	public RedisTemplate<String, ConnectionInfoDTO> redisTemplate2() {
		RedisTemplate<String, ConnectionInfoDTO> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());

		Jackson2JsonRedisSerializer<ConnectionInfoDTO> jsonSerializer = new Jackson2JsonRedisSerializer<>(
				ConnectionInfoDTO.class);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		jsonSerializer.setObjectMapper(objectMapper);

		template.setValueSerializer(jsonSerializer);
		template.afterPropertiesSet();

		return template;
	}
	
	


}
