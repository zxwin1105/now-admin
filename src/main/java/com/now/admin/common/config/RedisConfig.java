package com.now.admin.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class RedisConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ==========================
    // RedisTemplate 最新配置
    // ==========================
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // ✅ 新版本：JacksonJsonRedisSerializer（没有2！）
        JacksonJsonRedisSerializer<Object> serializer =
                new JacksonJsonRedisSerializer<>(objectMapper, Object.class);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }
//
//    // ==========================
//    // Spring Cache 最新配置
//    // ==========================
//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
//        StringRedisSerializer stringSerializer = new StringRedisSerializer();
//
//        JacksonJsonRedisSerializer<Object> serializer =
//                new JacksonJsonRedisSerializer<>(objectMapper, Object.class);
//
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofHours(2))
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
//                .computePrefixWith(cacheName -> "cache:" + cacheName + ":");
//
//        return RedisCacheManager.builder(factory)
//                .cacheDefaults(config)
//                .build();
//    }
}