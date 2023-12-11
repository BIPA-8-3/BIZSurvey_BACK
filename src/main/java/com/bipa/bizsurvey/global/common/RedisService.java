package com.bipa.bizsurvey.global.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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


    public <T> void saveData(String key, T data, Long duration){
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            String value = objectMapper.writeValueAsString(data);
            Duration expireDuration = Duration.ofSeconds(duration);
            redisTemplate.opsForValue().set(key, value, expireDuration);
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

    public String getData(String key) {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return (String) valueOperations.get(key);
    }

    public void validateDataExists(String key) {
        if(!redisTemplate.hasKey(key)) {
            throw new RuntimeException("유효하지 않은 key값 입니다.");
        }
    }
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
    public RedisTemplate<String, Object> getRedisTemplate() {
        return this.redisTemplate;
    }

}
