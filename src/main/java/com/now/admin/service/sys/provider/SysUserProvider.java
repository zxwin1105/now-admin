package com.now.admin.service.sys.provider;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import com.now.admin.service.sys.service.SysUserService;
import com.now.admin.service.sys.domain.SysUser;
import java.util.Optional;

@Service
public class SysUserProvider {

    @Resource
    private SysUserService sysUserService;

    /**
     * 通过主键ID查询系统用户信息
     * 
     * @param id 系统用户主键ID
     * @return Optional<SysUser> 系统用户信息
     */
    public Optional<SysUser> getById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        SysUser sysUser = sysUserService.getById(id);
        return Optional.ofNullable(sysUser);
    }

}
