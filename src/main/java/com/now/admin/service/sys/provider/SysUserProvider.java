package com.now.admin.service.sys.provider;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.now.admin.service.sys.domain.SysMenu;
import com.now.admin.service.sys.domain.SysRelation;
import com.now.admin.service.sys.domain.SysRole;
import com.now.admin.service.sys.mapper.SysRoleMapper;
import com.now.admin.service.sys.service.SysMenuService;
import com.now.admin.service.sys.service.SysRelationService;
import com.now.admin.service.sys.service.SysRoleService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import com.now.admin.service.sys.service.SysUserService;
import com.now.admin.service.sys.domain.SysUser;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SysUserProvider {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysRelationService sysRelationService;

    @Resource
    private SysRoleService sysRoleService;

    @Resource
    private SysMenuService sysMenuService;

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

    /**
     * 获取用户在系统中的角色权限信息
     * @param userId 用户ID
     * @return rbac信息  "roles" - set, "perms" - set
     */
    public Optional<Map<String, Set<String>>> getUserRBAC(Long userId){
        if(userId == null){
            return Optional.empty();
        }

        // 查询用户角色
        List<SysRelation> sysRoles = sysRelationService.queryRelation(userId, "user_role");
        if(CollectionUtils.isEmpty(sysRoles)){
            return Optional.empty();
        }
        List<Long> sysRoleIds = sysRoles.stream().map(SysRelation::getTargetId).toList();
        List<SysRelation> sysRelations = sysRelationService.queryRelation(sysRoleIds, "role_menu");
        List<Long> sysMenuIds = sysRelations.stream().map(SysRelation::getTargetId).toList();

        // 查询role 和 menu信息

        Set<String> roles = sysRoleService.list(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, sysRoleIds)).
                stream().map(SysRole::getRoleFlag)
                .filter(StringUtils::hasText).collect(Collectors.toSet());
        Set<String> perms = sysMenuService.list(new LambdaQueryWrapper<SysMenu>().in(SysMenu::getId, sysMenuIds))
                .stream()
                .map(SysMenu::getPerms)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        return Optional.of(
                Map.of(
                        "roles",
                        roles,
                        "perms",
                        perms
                )
        );

    }



}
