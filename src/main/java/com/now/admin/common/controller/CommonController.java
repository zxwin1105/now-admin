package com.now.admin.common.controller;

import com.now.admin.common.config.SecretKeyConfig;
import com.now.admin.common.domain.Result;
import jakarta.annotation.Resource;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Resource
    private SecretKeyConfig secretKeyConfig;

    @GetMapping("/get/public-key")
    public Result<String> getPublicKey(){
        return Result.success(secretKeyConfig.getPublicKeyStr());
    }

    private final ErrorAttributes errorAttributes;

    public CommonController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public ResponseEntity<Map<String, Object>> error(WebRequest request) {
        Map<String, Object> error = errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
        HttpStatus status = HttpStatus.valueOf((int) error.get("status"));

        // 404 直接返回 JSON，不再走静态资源
        return ResponseEntity.status(status).body(error);
    }
}
