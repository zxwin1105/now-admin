package com.now.admin.service.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.now.admin.service.sys.domain.SysUser;
import com.now.admin.service.sys.mapper.SysUserMapper;
import com.now.admin.service.sys.service.SysUserService;

import org.springframework.stereotype.Service;

/**
 * @author zhaixinwei
 * @description 针对表【sys_user(系统用户表)】的数据库操作Service实现
 * @createDate 2026-01-06 15:11:43
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

}
