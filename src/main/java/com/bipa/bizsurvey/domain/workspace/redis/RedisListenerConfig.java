package com.bipa.bizsurvey.domain.workspace.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisListenerConfig {
    @Value("${spring.redis.topic.expired}")
    private String expired;

    @Bean
    public ExpirationListener expirationListener() { return new ExpirationListener(); }

    @Bean
    public MessageListenerAdapter expirationListenerAdapter(ExpirationListener expirationListener) {return new MessageListenerAdapter(expirationListener);}

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connetcFactory,
                                                   MessageListenerAdapter listenerAdapter,
                                                   MessageListenerAdapter expirationListenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connetcFactory);

        container.addMessageListener(listenerAdapter, new PatternTopic("test"));
        container.addMessageListener(expirationListenerAdapter, new PatternTopic(expired));

        return container;
    }
}
