package com.now.admin.common.controller;

import com.now.admin.common.config.SecretKeyConfig;
import com.now.admin.common.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Resource
    private SecretKeyConfig secretKeyConfig;

    @GetMapping("/get/public-key")
    public Result<String> getPublicKey(){
        return Result.success(secretKeyConfig.getPublicKeyStr());
    }
}
