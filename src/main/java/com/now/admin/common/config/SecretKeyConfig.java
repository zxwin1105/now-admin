package com.now.admin.common.config;

import com.now.admin.common.exception.InnerCommonException;
import com.now.admin.common.util.RsaUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * 密钥配置类
 * 支持从多个来源加载RSA密钥对：
 * 1. 环境变量（最高优先级）
 * 2. 外部文件（推荐生产环境）
 * 3. 配置文件（开发测试环境）
 *
 * @author zhaixinwei
 * @date 2025/12/25
 */
@Slf4j
@Configuration
public class SecretKeyConfig {

    /**
     * 从配置文件读取的私钥（默认值）
     */
    @Value("${secret.private-key:}")
    private String configPrivateKey;

    /**
     * 从配置文件读取的公钥（默认值）
     */
    @Value("${secret.public-key:}")
    private String configPublicKey;

    /**
     * 外部密钥文件路径（可选）
     */
    @Value("${secret.key-file-path:./keys}")
    private String keyFilePath;

    /**
     * 是否启用外部文件加载
     */
    @Value("${secret.use-external-file:false}")
    private boolean useExternalFile;

    // Getter方法
    /**
     * 最终使用的私钥
     */
    @Getter
    private String privateKeyStr;

    /**
     * 最终使用的公钥
     */
    @Getter
    private String publicKeyStr;

    @Getter
    private PrivateKey privateKey;

    @Getter
    private PublicKey publicKey;

    /**
     * 初始化密钥
     * 优先级：环境变量 > 外部文件 > 配置文件
     */

    @PostConstruct
    public void init() {
        log.info("开始加载RSA密钥对...");

        try {
            // 1. 尝试从环境变量加载（最高优先级）
            String envPrivateKey = System.getenv("SECRET_PRIVATE_KEY");
            String envPublicKey = System.getenv("SECRET_PUBLIC_KEY");

            if (envPrivateKey != null && envPublicKey != null) {
                populateKeys(envPrivateKey, envPublicKey);
                log.info("✅ 从环境变量加载密钥成功");
                return;
            }

            // 2. 尝试从外部文件加载（推荐生产环境）
            if (useExternalFile) {
                try {
                    String privateKey = loadKeyFromFile(keyFilePath + "/private_key.pem");
                    String publicKey = loadKeyFromFile(keyFilePath + "/public_key.pem");
                    populateKeys(privateKey, publicKey);
                    log.info("✅ 从外部文件加载密钥成功: {}", keyFilePath);
                    return;
                } catch (Exception e) {
                    log.warn("⚠️ 从外部文件加载密钥失败，尝试使用配置文件: {}", e.getMessage());
                }
            }

            // 3. 使用配置文件中的密钥（开发测试环境）
            if (configPrivateKey != null && !configPrivateKey.isEmpty()) {
                populateKeys(configPrivateKey, configPublicKey);
                log.info("✅ 从配置文件加载密钥成功");
                return;
            }

            throw new InnerCommonException("❌ 无法加载RSA密钥对，请检查配置！");

        } catch (Exception e) {
            log.error("❌ 密钥加载失败", e);
            throw new InnerCommonException("密钥加载失败: " + e.getMessage());
        }
    }

    /**
     * 从文件加载密钥
     */
    private String loadKeyFromFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new IOException("密钥文件不存在: " + filePath);
        }
        return new String(Files.readAllBytes(path)).trim();
    }

    /**
     * 热更新密钥（从外部文件重新加载）
     * 可用于密钥轮换场景
     */
    public void reloadKeys() throws InnerCommonException {
        log.info("🔄 开始热更新密钥...");
        if (useExternalFile) {
            try {
                String privateKey = loadKeyFromFile(keyFilePath + "/private_key.pem");
                String publicKey = loadKeyFromFile(keyFilePath + "/public_key.pem");
                populateKeys(privateKey, publicKey);
                log.info("✅ 密钥热更新成功");
            } catch (Exception e) {
                log.error("❌ 密钥热更新失败", e);
                throw new InnerCommonException("密钥热更新失败: " + e.getMessage());
            }
        } else {
            throw new InnerCommonException("未启用外部文件模式，无法热更新");
        }
    }

    private void populateKeys(String privateKey, String publicKey) {
        this.privateKeyStr = privateKey;
        this.publicKeyStr = publicKey;
        this.privateKey = RsaUtil.getPrivateKey(privateKey);
        this.publicKey = RsaUtil.getPublicKey(publicKey);
    }

}
