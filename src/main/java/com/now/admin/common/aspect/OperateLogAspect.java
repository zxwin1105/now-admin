package com.now.admin.common.aspect;



import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.logging.LogRecord;

/**
 * 操作日志切面
 * 
 * 该切面通过记录用户通过Controller执行的系统操作
 * 默认解析@Operation注解，获取操作类型、操作描述等信息
 * 只记录其中一下操纵：
 * 1. 新增 add*
 * 2. 删除 delete*
 * 3. 修改 update*
 * 4. 查询 query*
 * 5. 导出 export*
 * 6. 导入 import*
 * 
 * 要被日志切面记录，必须遵循Controller的命名规范
 * 即：Controller的方法名必须以add、delete、update、query、export、import开头
 */
@Aspect
@Component
public class OperateLogAspect {

    /**
     * 操作日志记录方法前缀列表
     * 
     * 该列表包含了所有需要记录操作日志的方法前缀
     * 即：Controller的方法名必须以add、delete、update、query、export、import开头
     */
    private final List<String> LOG_METHODS_PREFIX = Arrays.asList("add", "delete", "update", "query", "export",
            "import");

    /**
     * 操作日志记录
     * 
     * 该方法在Controller方法执行后被调用，用于记录操作日志
     * 操作日志包含操作类型、操作描述、操作人、操作时间等信息
     * 
     * @param joinPoint 连接点，用于获取方法执行信息
     */
    @After("execution(* com.now.admin.*.*.controller.*.*(..))")
    public void logOperate(JoinPoint joinPoint) {
        // === 过滤Controller中不需要执行日志方法 ===
        String methodName = joinPoint.getSignature().getName();
        boolean isLogMethod = needLog(methodName);
        if (!isLogMethod) {
            return;
        }

        // === 记录操作日志 ===

        // 从SecurityContext中获取操作用户信息

        // 获取方法上的Operation注解

    }

    /**
     * 判断是否需要记录操作日志
     * 
     * @param methodName 方法名
     * @return 是否需要记录操作日志
     */
    private boolean needLog(String methodName) {
        return LOG_METHODS_PREFIX.stream().anyMatch(methodName::startsWith);
    }
    
    /**
     * 根据方法名获取操作类型
     *
     * @param methodName 方法名
     * @return 操作类型
     */
    private String getOperateTypeByMethodName(String methodName) {
        if (methodName.startsWith("add")) {
            return "新增";
        } else if (methodName.startsWith("delete")) {
            return "删除";
        } else if (methodName.startsWith("update")) {
            return "修改";
        } else if (methodName.startsWith("query")) {
            return "查询";
        } else if (methodName.startsWith("export")) {
            return "导出";
        } else if (methodName.startsWith("import")) {
            return "导入";
        }
        return "其他";
    }
    


}
