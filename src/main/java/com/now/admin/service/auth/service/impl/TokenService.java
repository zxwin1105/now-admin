package com.now.admin.service.auth.service.impl;

import com.now.admin.common.config.SecretKeyConfig;
import com.now.admin.common.constant.AppStatusEnum;
import com.now.admin.common.exception.InnerCommonException;
import com.now.admin.service.auth.common.exception.AuthenticateException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
public class TokenService {

    @Resource
    private SecretKeyConfig secretKeyConfig;

    // Token过期时间（毫秒）
    private final Long EXPIRATION = 1000 * 60 * 60 * 24 * 7L;

    // Token刷新时间（毫秒）
    private final Long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 30L;


    private final String CLAIM_KEY_USER_ID = "userId";

    private final String CLAIM_KEY_CREATED = "created";


    /**
     * 生成Token
     * @param userId 用户ID
     * @return  Token
     */
    public String generateToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims, EXPIRATION);
    }

    /**
     * 生成刷新Token
     * @param userId 用户ID
     * @return 刷新Token
     */
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims, REFRESH_EXPIRATION);
    }

    /**
     * 刷新Token
     * @param token 刷新Token
     * @return Optional<String>
     */
    public Optional<String> refreshToken(String token) {
        Date expirationDateFromToken = getExpirationDateFromToken(token);
        if(expirationDateFromToken.before(new Date())){
            return Optional.empty();
        }
        Long userId = getUserIdFromToken(token);
        if(userId == null){
            return Optional.empty();
        }
        return Optional.of(generateToken(userId));
    }


    private String generateToken(Map<String, Object> claims, Long refreshExpiration) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + refreshExpiration);
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expirationDate)
                // 使用私钥签名
                .signWith(secretKeyConfig.getPrivateKey())
                .compact();
    }

    // ================= 验证token =================

    /**
     * 验证Token是否有效（不比对用户名）
     *
     * @param token JWT Token
     * @return true=有效，false=无效
     */
    public boolean validateToken(String token) {
        getClaimsFromToken(token);
        return !isTokenExpired(token);
    }

    /**
     * 验证Token是否有效
     *
     * @param token    JWT Token
     * @return true=有效，false=无效
     */
    public boolean validateToken(String token, Long userId) {
        Long tokenUserId = getUserIdFromToken(token);
        return (Objects.equals(tokenUserId, userId) && !isTokenExpired(token));

    }

    /**
     * 判断Token是否已过期
     * @param token JWT Token
     * @return true=已过期，false=未过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());

    }


    // ================= 解析token =================

    /**
     * 从Token中获取所有Claims
     *
     * @param token JWT Token
     * @return Claims对象
     * @throws InnerCommonException Token解析失败时抛出
     */
    public Claims getClaimsFromToken(String token) throws AuthenticateException {
        try {
            return Jwts.parser()
                    .verifyWith(secretKeyConfig.getPublicKey())  // 使用RSA公钥验证签名
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AuthenticateException(AppStatusEnum.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new AuthenticateException("Token格式错误");
        } catch (SignatureException e) {
            throw new AuthenticateException("Token签名验证失败");
        } catch (Exception e) {
            throw new AuthenticateException("Token解析失败: " + e.getMessage());
        }
    }

    /**
     * 从Token中获取过期时间
     *
     * @param token JWT Token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) throws InnerCommonException {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从Token中获取用户ID
     * @param token JWT Token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) throws InnerCommonException {
        Claims claims = getClaimsFromToken(token);
        Object userId = claims.get(CLAIM_KEY_USER_ID);
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }
    /**
     * 从Token中获取指定claim
     *
     * @param token          JWT Token
     * @param claimsResolver 解析函数
     * @return claim值
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws InnerCommonException {
        Claims claims = getClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

}
