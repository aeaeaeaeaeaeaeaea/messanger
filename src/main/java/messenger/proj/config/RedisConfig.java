package messenger.proj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import messenger.proj.models.message;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, message> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, message> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // Дополнительные настройки RedisTemplate, если необходимо
        return template;
    }
}

