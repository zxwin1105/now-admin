package com.now.admin.common.util;

import com.now.admin.common.exception.InnerCommonException;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA非对称加密工具类
 * 用于Token及敏感数据的加密、解密、签名、验证
 *
 * @author zhaixinwei
 * @date 2025/12/25
 */
public class RsaUtil {

    /**
     * RSA算法标识
     */
    private static final String ALGORITHM = "RSA";

    /**
     * RSA加密算法（默认模式）
     */
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    /**
//     * 默认密钥长度（2048位安全性高，1024位性能好）
     */
    private static final int KEY_SIZE = 2048;

    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * 生成RSA密钥对
     *
     * @param keySize 密钥长度（建议2048或4096）
     * @return 包含公钥和私钥的Map，key为"publicKey"和"privateKey"
     * @throws InnerCommonException 密钥生成失败时抛出
     */
    public static Map<String, String> generateKeyPair(int keySize) throws InnerCommonException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // 获取公钥和私钥
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            // 转为Base64编码字符串
            String publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            Map<String, String> keyMap = new HashMap<>();
            keyMap.put("publicKey", publicKeyStr);
            keyMap.put("privateKey", privateKeyStr);
            return keyMap;
        } catch (NoSuchAlgorithmException e) {
            throw new InnerCommonException("RSA密钥对生成失败: " + e.getMessage());
        }
    }

    /**
     * 生成RSA密钥对（默认2048位）
     *
     * @return 包含公钥和私钥的Map
     */
    public static Map<String, String> generateKeyPair() throws InnerCommonException {
        return generateKeyPair(KEY_SIZE);
    }

    /**
     * 公钥加密
     *
     * @param data         待加密的数据
     * @param publicKeyStr Base64编码的公钥字符串
     * @return Base64编码的加密数据
     * @throws InnerCommonException 加密失败时抛出
     */
    public static String encrypt(String data, String publicKeyStr) throws InnerCommonException {
        try {
            // 解码公钥
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr.trim());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);

            // 加密
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedData = cipher.doFinal(data.getBytes("UTF-8"));

            // 返回Base64编码的加密数据
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new InnerCommonException("RSA公钥加密失败: " + e.getMessage());
        }
    }

    /**
     * 私钥解密
     *
     * @param encryptedData Base64编码的加密数据
     * @param privateKeyStr Base64编码的私钥字符串
     * @return 解密后的原始数据
     * @throws InnerCommonException 解密失败时抛出
     */
    public static String decrypt(String encryptedData, String privateKeyStr) throws InnerCommonException {
        try {
            // 解码私钥
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr.trim());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

            // 解密
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData.trim()));

            return new String(decryptedData, "UTF-8");
        } catch (Exception e) {
            throw new InnerCommonException("RSA私钥解密失败: " + e.getMessage());
        }
    }

    /**
     * 私钥签名（用于Token签名）
     *
     * @param data          待签名的数据
     * @param privateKeyStr Base64编码的私钥字符串
     * @return Base64编码的签名
     * @throws InnerCommonException 签名失败时抛出
     */
    public static String sign(String data, String privateKeyStr) throws InnerCommonException {
        try {
            // 解码私钥
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr.trim());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            // 签名
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data.getBytes("UTF-8"));
            byte[] signBytes = signature.sign();

            return Base64.getEncoder().encodeToString(signBytes);
        } catch (Exception e) {
            throw new InnerCommonException("RSA签名失败: " + e.getMessage());
        }
    }

    /**
     * 公钥验证签名（用于Token验证）
     *
     * @param data         原始数据
     * @param sign         Base64编码的签名
     * @param publicKeyStr Base64编码的公钥字符串
     * @return 验证是否通过
     * @throws InnerCommonException 验证失败时抛出
     */
    public static boolean verify(String data, String sign, String publicKeyStr) throws InnerCommonException {
        try {
            // 解码公钥
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr.trim());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // 验证签名
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(data.getBytes("UTF-8"));
            return signature.verify(Base64.getDecoder().decode(sign.trim()));
        } catch (Exception e) {
            throw new InnerCommonException("RSA签名验证失败: " + e.getMessage());
        }
    }

    /**
     * 从Base64字符串获取私钥对象（用于JWT）
     *
     * @param privateKeyStr Base64编码的私钥字符串
     * @return PrivateKey对象
     * @throws InnerCommonException 解析失败时抛出
     */
    public static PrivateKey getPrivateKey(String privateKeyStr) throws InnerCommonException {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr.trim());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new InnerCommonException("私钥解析失败: " + e.getMessage());
        }
    }

    /**
     * 从Base64字符串获取公钥对象（用于JWT）
     *
     * @param publicKeyStr Base64编码的公钥字符串
     * @return PublicKey对象
     * @throws InnerCommonException 解析失败时抛出
     */
    public static PublicKey getPublicKey(String publicKeyStr) throws InnerCommonException {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr.trim());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new InnerCommonException("公钥解析失败: " + e.getMessage());
        }
    }

    /**
     * 测试方法：生成密钥对并演示加密解密
     */
    public static void main(String[] args) {
        try {
            System.out.println("========== RSA密钥对生成 ==========");
            Map<String, String> keyPair = generateKeyPair();
            String publicKey = keyPair.get("publicKey");
            String privateKey = keyPair.get("privateKey");

            System.out.println("公钥 (Public Key):");
            System.out.println(publicKey);
            System.out.println("\n私钥 (Private Key):");
            System.out.println(privateKey);

            System.out.println("\n========== 加密解密测试 ==========");
            String originalData = "这是一个Token数据";
            System.out.println("原始数据: " + originalData);

            // 公钥加密
            String encryptedData = encrypt(originalData, publicKey);
            System.out.println("加密数据: " + encryptedData);

            // 私钥解密
            String decryptedData = decrypt(encryptedData, privateKey);
            System.out.println("解密数据: " + decryptedData);

            System.out.println("\n========== 签名验证测试 ==========");
            String dataToSign = "用户Token信息";
            System.out.println("待签名数据: " + dataToSign);

            // 私钥签名
            String signature = sign(dataToSign, privateKey);
            System.out.println("签名: " + signature);

            // 公钥验证
            boolean isValid = verify(dataToSign, signature, publicKey);
            System.out.println("签名验证结果: " + (isValid ? "通过✓" : "失败✗"));

        } catch (InnerCommonException e) {
            e.printStackTrace();
        }
    }
}