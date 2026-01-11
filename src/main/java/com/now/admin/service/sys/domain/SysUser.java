package com.now.admin.service.sys.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 系统用户表
 * 
 * @TableName sys_user
 */
@TableName(value = "sys_user")
@Data
public class SysUser {
    /**
     * 自增逐渐
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 系统生成，用于唯一标识用户
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 性别
     */
    @TableField(value = "gender")
    private String gender;

    /**
     * 用户状态 1:正常 0:禁用 -1:异常
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 删除标志 1:正常 -1:已删除
     */
    @TableField(value = "deleted_flag")
    private Integer deletedFlag;

    /**
     * 生日
     */
    @TableField(value = "birthday")
    private LocalDateTime birthday;

    /**
     * 生日是否农历 1:是 -1:否
     */
    @TableField(value = "lunar_calendar")
    private Integer lunarCalendar;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

}