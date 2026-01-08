package com.now.admin;

import com.now.admin.service.auth.service.impl.TokenService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@MapperScan("com.now.admin.service.**.mapper")
@SpringBootApplication
public class NowAdminApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(NowAdminApplication.class, args);
        TokenService bean = run.getBean(TokenService.class);
        String token = bean.generateToken(2L);
        System.out.println(token);

    }

}
