package com.now.admin.service.auth.controller;


import com.now.admin.common.domain.Result;
import com.now.admin.common.domain.vo.LoginRsp;
import com.now.admin.service.auth.common.util.AuthContextHolder;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.domain.param.LoginUserParam;
import com.now.admin.service.auth.domain.param.RegisterUserParam;
import com.now.admin.service.auth.domain.param.SendSmsCodeParam;
import com.now.admin.service.auth.service.AuthService;
import com.now.admin.service.auth.service.SmsCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证接口", description = "用户登录、注册、验证码等认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;
    
    @Resource
    private SmsCodeService smsCodeService;

    @Operation(summary = "用户注册", description = "通过用户名密码注册新用户")
    @PostMapping("/register")
    public Result<String> registerUser(@RequestBody RegisterUserParam registerUserParam)
    {
        return Result.success();
    }

    @Operation(summary = "用户名密码登录", description = "传统的用户名密码登录方式")
    @PostMapping("/login")
    public Result<LoginRsp> login(@Valid @RequestBody LoginUserParam loginUserParam){
        LoginRsp loginRsp = authService.authenticate(loginUserParam);
        return Result.success(loginRsp);
    }
    
    @Operation(summary = "发送短信验证码", description = "向指定手机号发送验证码")
    @PostMapping("/sms/send")
    public Result<String> sendSmsCode(@Valid @RequestBody SendSmsCodeParam param) {
        String code = smsCodeService.sendSmsCode(param.getPhone());
        // 测试环境返回验证码，生产环境不应返回
        return Result.success("验证码已发送，测试码: " + code);
    }

    @GetMapping("/get")
    public Result<LoginUserDetail> getUser(){
        System.out.println("get");
        return Result.success(AuthContextHolder.getCurrentUser());
    }



}
