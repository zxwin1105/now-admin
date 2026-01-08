package com.now.admin.service.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 短信验证码服务
 * 注意：这是简化版本，生产环境应使用Redis存储验证码，并集成真实的短信服务商
 */
@Slf4j
@Service
public class SmsCodeService {
    
    /**
     * 验证码存储（生产环境应使用Redis）
     * key: 手机号, value: 验证码
     */
    private final Map<String, String> codeCache = new ConcurrentHashMap<>();
    
    /**
     * 验证码过期时间存储
     * key: 手机号, value: 过期时间戳
     */
    private final Map<String, Long> expireCache = new ConcurrentHashMap<>();
    
    /**
     * 发送频率限制存储
     * key: 手机号, value: 上次发送时间戳
     */
    private final Map<String, Long> sendTimeCache = new ConcurrentHashMap<>();
    
    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 6;
    
    /**
     * 验证码有效期（毫秒），默认5分钟
     */
    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000;
    
    /**
     * 发送间隔限制（毫秒），默认60秒
     */
    private static final long SEND_INTERVAL = 60 * 1000;
    
    /**
     * 发送短信验证码
     * 
     * @param phone 手机号
     * @return 验证码（生产环境不应返回，仅用于测试）
     */
    public String sendSmsCode(String phone) {
        // 校验手机号格式
        if (!isValidPhoneNumber(phone)) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
        
        // 检查发送频率
        Long lastSendTime = sendTimeCache.get(phone);
        long currentTime = System.currentTimeMillis();
        if (lastSendTime != null && (currentTime - lastSendTime) < SEND_INTERVAL) {
            long waitSeconds = (SEND_INTERVAL - (currentTime - lastSendTime)) / 1000;
            throw new IllegalArgumentException("验证码发送过于频繁，请" + waitSeconds + "秒后重试");
        }
        
        // 生成验证码
        String code = generateCode();
        
        // 存储验证码和过期时间
        codeCache.put(phone, code);
        expireCache.put(phone, currentTime + CODE_EXPIRE_TIME);
        sendTimeCache.put(phone, currentTime);
        
        log.info("发送短信验证码到手机号: {}, 验证码: {} (测试环境)", phone, code);
        
        // TODO: 集成真实短信服务商（阿里云SMS、腾讯云SMS等）
        // smsClient.send(phone, code);
        
        return code; // 生产环境不应返回验证码
    }
    
    /**
     * 验证短信验证码
     * 
     * @param phone 手机号
     * @param code 验证码
     * @return 验证是否成功
     */
    public boolean verifySmsCode(String phone, String code) {
        if (phone == null || code == null) {
            return false;
        }
        
        // 获取存储的验证码
        String storedCode = codeCache.get(phone);
        Long expireTime = expireCache.get(phone);
        
        if (storedCode == null || expireTime == null) {
            log.warn("手机号 {} 的验证码不存在", phone);
            return false;
        }
        
        // 检查是否过期
        if (System.currentTimeMillis() > expireTime) {
            log.warn("手机号 {} 的验证码已过期", phone);
            // 清除过期验证码
            codeCache.remove(phone);
            expireCache.remove(phone);
            return false;
        }
        
        // 验证码比对
        boolean isValid = storedCode.equals(code);
        
        if (isValid) {
            log.info("手机号 {} 验证码验证成功", phone);
            // 验证成功后删除验证码（一次性使用）
            codeCache.remove(phone);
            expireCache.remove(phone);
        } else {
            log.warn("手机号 {} 验证码验证失败", phone);
        }
        
        return isValid;
    }
    
    /**
     * 生成随机验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
    
    /**
     * 校验手机号格式（简单校验）
     */
    private boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        // 简单的手机号格式校验：11位数字，1开头
        return phone.matches("^1[3-9]\\d{9}$");
    }
}
