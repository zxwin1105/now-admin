package com.now.admin.service.sys.provider;

import com.now.admin.common.domain.LogRecord;
import com.now.admin.service.sys.domain.SysOperationLog;
import com.now.admin.service.sys.service.SysOperationLogService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class SysOperationLogProvider {

    @Resource
    private SysOperationLogService sysOperationLogService;

    public void saveOperationLog(LogRecord logRecord) {
        SysOperationLog sysOperationLog = new SysOperationLog();
        BeanUtils.copyProperties(logRecord, sysOperationLog);
        try {
            sysOperationLogService.save(sysOperationLog);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
