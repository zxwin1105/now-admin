package com.now.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import tools.jackson.databind.json.JsonMapper;


@MapperScan("com.now.admin.service.**.mapper")
@SpringBootApplication
public class NowAdminApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(NowAdminApplication.class, args);
        System.out.println(run.getBean(JsonMapper.class));
    }

}
