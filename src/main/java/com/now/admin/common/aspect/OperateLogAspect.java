package com.now.admin.common.aspect;


import com.now.admin.common.domain.LogRecord;
import com.now.admin.common.util.JsonUtil;
import com.now.admin.service.auth.domain.LoginUserDetail;
import com.now.admin.service.auth.provider.AuthProvider;
import com.now.admin.service.sys.provider.SysOperationLogProvider;
import com.now.admin.service.sys.service.SysOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
@Slf4j
@Aspect
@Component
public class OperateLogAspect {

    @Resource
    private AuthProvider authProvider;

    @Resource
    private SysOperationLogProvider sysOperationLogProvider;

    /**
     * 操作日志记录
     *
     * 该方法在Controller方法执行后被调用，用于记录操作日志
     * 操作日志包含操作类型、操作描述、操作人、操作时间等信息
     * 只对方法名以add、delete、update、query、export、import开头的Controller方法进行记录
     *
     * @param joinPoint 连接点，用于获取方法执行信息
     * @param result 方法执行结果
     */
    @AfterReturning(
        pointcut = "execution(* com.now.admin.*.*.controller.*.add*(..)) || " +
                   "execution(* com.now.admin.*.*.controller.*.delete*(..)) || " +
                   "execution(* com.now.admin.*.*.controller.*.update*(..)) || " +
                   "execution(* com.now.admin.*.*.controller.*.query*(..)) || " +
                   "execution(* com.now.admin.*.*.controller.*.export*(..)) || " +
                   "execution(* com.now.admin.*.*.controller.*.import*(..))",
        returning = "result")
    public void logOperate(JoinPoint joinPoint, Object result) {
        try{
            // === 记录操作日志 ===
            LogRecord logRecord = new LogRecord();

            // 从SecurityContext中获取操作用户信息
            populateOptUser(logRecord);
            // 获取方法上的Operation注解和参数信息
            populateOpt(joinPoint, logRecord);

            logRecord.setOperateResult(JsonUtil.toJson(result));
            // 设置操作时间
            logRecord.setOperateTime(LocalDateTime.now());
            saveRecord(logRecord);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    /**
     * 保存操作日志
     * @param logRecord 操作日志记录
     */
    private void saveRecord(LogRecord logRecord) {
        sysOperationLogProvider.saveOperationLog(logRecord);
    }

    /**
     * 填充操作信息
     * @param logRecord 操作日志记录
     * @param joinPoint 连接点，用于获取方法执行信息
     */
    private void populateOpt(JoinPoint joinPoint, LogRecord logRecord) {


        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 方法参数
        Parameter[] parameters = method.getParameters();

        Object[] args = joinPoint.getArgs();

        // 封装为map
        Map<String, Object> paramsMap = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            paramsMap.put(parameters[i].getName(), args[i]);
        }

        String params = JsonUtil.toJson(paramsMap);
        logRecord.setOperateParams(params);

        // 解析方法上的Operation注解
        Operation operation = method.getAnnotation(Operation.class);

        String methodName = joinPoint.getSignature().getName();
        logRecord.setOperateType(getOperateTypeByMethodName(methodName));

        if (operation != null) {
            logRecord.setOperateType(getOperateTypeByMethodName(methodName));
            logRecord.setOperation(operation.summary());
            logRecord.setOperateDesc(operation.description());
        }
    }

    /**
     * 填充操作用户信息
      * @param logRecord 操作日志记录
     */
    private void populateOptUser(LogRecord logRecord) {
        Optional<LoginUserDetail> loginUserDetailOptional = authProvider.getLoginUserDetail();
        if (loginUserDetailOptional.isPresent()) {
            LoginUserDetail loginUserDetail = loginUserDetailOptional.get();

            logRecord.setId(loginUserDetail.getId());
            logRecord.setUsername(loginUserDetail.getUsername());
            logRecord.setIp(loginUserDetail.getIp());
            logRecord.setUserAgent(loginUserDetail.getUserAgent());
        }
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