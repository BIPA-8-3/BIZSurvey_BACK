package com.bipa.bizsurvey.domain.workspace.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisPublisherConfig {
    @Bean
    public Publisher redisPublisher() {
        return new Publisher();
    }
}