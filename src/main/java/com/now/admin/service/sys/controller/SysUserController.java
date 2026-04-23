package com.now.admin.service.sys.controller;

import com.now.admin.common.domain.Result;
import com.now.admin.service.sys.domain.SysUser;
import com.now.admin.service.sys.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;


    @GetMapping("/get/{id}")
    public Result<SysUser> getUserById(@PathVariable Long id){
        return  Result.success(sysUserService.getUserById(id));
    }

}
