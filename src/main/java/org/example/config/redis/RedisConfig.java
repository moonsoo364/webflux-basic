package org.example.config.redis;

import lombok.RequiredArgsConstructor;
import org.example.auth.model.Member;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ReactiveRedisConnectionFactory redisConnectionFactory;

    @Bean(name = "redisMemberCache")
    public ReactiveRedisTemplate<String, Member> redisMemberCache() {

        RedisSerializationContext<String, Member> context =
                RedisSerializationContext.<String, Member>newSerializationContext(new StringRedisSerializer())
                        .value(new Jackson2JsonRedisSerializer<>(Member.class))
                        .build();

        return new ReactiveRedisTemplate<>(redisConnectionFactory, context);
    }
}

