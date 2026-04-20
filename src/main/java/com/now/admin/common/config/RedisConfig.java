package com.now.admin.common.config;

import jakarta.annotation.Resource;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.util.Collections;

@Configuration
@EnableCaching
public class RedisConfig {

    private final JsonMapper jsonMapper;

    public RedisConfig(){
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
                // 可以安全序列化白名单
                .allowIfSubType("com.now.admin.")
                .allowIfBaseType("com.now.admin.")
                .allowIfSubType("java.")
                .build();
        this.jsonMapper = JsonMapper.builder()
                .activateDefaultTyping(
                    ptv, DefaultTyping.NON_FINAL
                )
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        GenericJacksonJsonRedisSerializer serializer =
                new GenericJacksonJsonRedisSerializer(jsonMapper);

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();

        return template;
    }

    // ==========================
    // Spring Cache 最新配置
    // ==========================
//    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory factory) {
        return RedisCacheManager.builder(factory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
                .transactionAware()
                .withInitialCacheConfigurations(Collections.singletonMap("predefined",
                        RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues()))
                .build();
    }
}
