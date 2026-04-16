package com.now.admin.service.sys.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统操作日志
 *
 * @TableName sys_operation_log
 */
@TableName(value = "sys_operation_log", autoResultMap = true)
@Data
public class SysOperationLog {
    /**
     * 主键id
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 操作用户ID
     */
    @TableField(value = "user_id")
    private String userId;

    /**
     * 操作用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 操作类型
     */
    @TableField(value = "operate_type")
    private String operateType;

    /**
     * 操作内容
     */
    @TableField(value = "operation")
    private String operation;

    /**
     * 操作描述
     */
    @TableField(value = "operate_desc")
    private String operateDesc;

    /**
     * 方法执行参数
     */
    @TableField(value = "operate_params")
    private String operateParams;

    /**
     * 操作执行结果
     */
    @TableField(value = "operate_result")
    private String operateResult;

    /**
     * 操作设备Ip
     */
    @TableField(value = "ip")
    private Integer ip;

    /**
     *
     */
    @TableField(value = "user_agent")
    private String userAgent;

    /**
     * 操作时间
     */
    @TableField(value = "operate_time")
    private LocalDateTime operateTime;
}