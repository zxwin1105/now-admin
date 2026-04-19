package com.now.admin;

import com.now.admin.common.constant.RedisKeyConstant;
import com.now.admin.common.util.RedisUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@MapperScan("com.now.admin.service.**.mapper")
@SpringBootApplication
public class NowAdminApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(NowAdminApplication.class, args);


        LettuceConnectionFactory factory = new LettuceConnectionFactory();
        factory.afterPropertiesSet();

        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setDefaultSerializer(StringRedisSerializer.UTF_8);
        template.afterPropertiesSet();

        template.opsForValue().set("test","test");

        factory.destroy();
    }
}
