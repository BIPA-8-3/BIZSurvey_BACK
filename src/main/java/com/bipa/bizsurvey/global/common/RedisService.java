package com.bipa.bizsurvey.global.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public <T> void saveData(String key, T data){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(key, value);
        }catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public <T> Optional<T> getData(String key, Class<T> classType){
        String value = (String)redisTemplate.opsForValue().get(key);

        if(value == null){
            return Optional.empty();
        }

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            T data = objectMapper.readValue(value, classType);
            return Optional.of(data);
        }catch (Exception e){
            log.error(e.getMessage());
            return Optional.empty();
        }
    }
}
