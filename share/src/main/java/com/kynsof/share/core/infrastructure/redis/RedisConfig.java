package com.kynsof.share.core.infrastructure.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    
    @Value("${REDIS_ADDRESS:localhost}")
    private String redisAddress;
    
    @Value("${REDIS_PORT:6379}")
    private Integer redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(redisAddress, redisPort));
    }
}