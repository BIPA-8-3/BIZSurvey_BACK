package com.bipa.bizsurvey.domain.workspace.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

public class Publisher {
    @Autowired
    private StringRedisTemplate redisTemplate;
    private String topic = "test";

    public void publish(String message) {
        redisTemplate.convertAndSend(topic, message);
    }
}
