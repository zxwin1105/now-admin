package com.now.admin.service.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.now.admin.service.sys.domain.SysUser;

/**
 * @author zhaixinwei
 * @description 针对表【sys_user(系统用户表)】的数据库操作Service
 * @createDate 2026-01-06 15:11:43
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 新增系统用户
     * @param sysUser sysUser
     * @return boolean
     */
    boolean saveUser(SysUser sysUser);

    /**
     * 删除系统用户
     * @param id 用户ID
     * @return boolean
     */
    boolean removeUserById(Long id);

    /**
     * 修改用户信息
     * @param sysUser 用户信息
     * @return boolean
     */
    boolean modifyUser(SysUser sysUser);

    SysUser getUserById(Long id);

}
