package com.bipa.bizsurvey.global.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

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

    public Set<String> scanKeys(String pattern) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
        Cursor<byte[]> cursor = redisTemplate.getConnectionFactory().getConnection().scan(options);

        Set<String> keys = new HashSet<>();
        while (cursor.hasNext()) {
            keys.add(new String(cursor.next()));
        }

        return keys;
    }
}
