package com.now.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan("com.now.admin.service.**.mapper")
@SpringBootApplication
public class NowAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(NowAdminApplication.class, args);
    }

}
