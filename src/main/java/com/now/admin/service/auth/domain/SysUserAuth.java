package com.now.admin.service.auth.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户认证信息
 * @TableName sys_user_auth
 */
@TableName(value ="sys_user_auth")
@Data
public class SysUserAuth {
    /**
     * 认证 ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户ID（主键id）
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 认证类型 pwd,phone,email,oauth2
     */
    @TableField(value = "identifier_type")
    private String identifierType;

    /**
     * 身份标识
     */
    @TableField(value = "identifier")
    private String identifier;

    /**
     * 登录凭证
     */
    @TableField(value = "credential")
    private String credential;

    /**
     * 盐值
     */
    @TableField(value = "salt")
    private String salt;

    /**
     * 删除标志 1:正常 -1:已删除
     */
    @TableField(value = "delete_flag")
    private Integer deleteFlag;

    /**
     * 状态 1:正常 0:禁用 -1:异常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 	创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;
}