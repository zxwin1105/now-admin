package com.now.admin.common.domain;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志记录实体类
 * 
 * 该实体类用于存储操作日志记录信息
 * 包含操作类型、操作描述、操作人、操作时间等字段
 */
@Data
public class LogRecord {

    // === 操作用户相关 ===
    /**
     * 操作用户ID
     */
    private Long id;


    /**
     * 操作用户名
     */
    private String username;

    // === 操作执行方法 ===

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作内容
     */
    private String operation;

    /**
     * 操作描述
     */
    private String operateDesc;

    /**
     * 操作参数
     */
    private String operateParams;

    /**
     * 操作结果
     */
    private String operateResult;

    // === 操作设备 ===

    /**
     * 操作IP
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作时间
     */
    private LocalDateTime operateTime;

}
