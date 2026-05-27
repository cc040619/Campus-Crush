package com.cc.campuscrush.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * 【JwtUtil】工具类
 * &lt;p&gt;核心功能：JWT Token工具类，基于jjwt库提供Token生成（HS256签名+自定义claims+过期时间）、解析（提取username/userId）和验证（签名+过期双重校验）等核心方法&lt;/p&gt;
 * &lt;p&gt;使用场景：被UserLoginInterceptor拦截器调用以验证请求Token有效性，被登录Service调用以生成登录Token，密钥和过期时间从application.yml配置文件中读取&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Component
public class JwtUtil {

    // 密钥（32位以上，建议自己修改）
    //public static final String SECRET_KEY = "my-secret-key-12345678901234567890";

    // 过期时间：24小时
    //private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // 过期时间：1分钟（测试用）
    // private static final long EXPIRATION_TIME = 1000 * 60;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    /**
     * 生成JWT Token
     * 业务逻辑：将username追加到claims声明集合 → 调用createJWT静态方法（HS256签名+过期时间）→ 返回紧凑的JWT字符串
     *
     * @param claims   自定义声明Map（如userId等业务字段，可空Map）
     * @param username 用户名（必填）
     * @return JWT Token字符串
     */
    public String generateToken(Map<String, Object> claims, String username) {
        // 添加用户名到claims
        claims.put("username", username);
        return createJWT(secretKey, expirationTime, claims);
    }

    /**
     * 从Token中提取用户名
     * 业务逻辑：解析Token获取Claims → 从Claims中提取username字段作为String返回
     * 异常场景：Token格式不正确或签名无效时，parseJWT抛出异常，由调用方处理
     *
     * @param token JWT Token字符串（必填）
     * @return 用户名，Token中无username字段时返回null
     */
    public String extractUsername(String token) {
        Claims claims = parseJWT(secretKey, token);
        return claims.get("username", String.class);
    }

    /**
     * 从Token中提取用户ID
     * 业务逻辑：解析Token获取Claims → 从Claims中提取userId字段作为Long返回
     * 异常场景：Token格式不正确、签名无效或userId字段缺失/类型不匹配时抛出异常
     *
     * @param token JWT Token字符串（必填）
     * @return 用户ID，Token中无userId字段时返回null
     */
    public Long extractUserId(String token) {
        Claims claims = parseJWT(secretKey, token);
        return claims.get("userId", Long.class);
    }

    /**
     * 验证Token有效性（用户名匹配+未过期双重校验）
     * 业务逻辑：解析Token → 提取username → 验证username是否匹配 → 验证过期时间是否在当前时间之后
     * 异常场景：Token无效、签名错误、过期、username不匹配返回false，解析异常返回false，不抛出
     *
     * @param token    JWT Token字符串（必填）
     * @param username 预期用户名（必填）
     * @return true表示Token有效且用户名匹配，false表示无效
     */
    public boolean validateToken(String token, String username) {
        try {
            Claims claims = parseJWT(secretKey, token);
            String extractedUsername = claims.get("username", String.class);
            Date expiration = claims.getExpiration();
            return extractedUsername != null && extractedUsername.equals(username) && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断Token是否已过期
     * 业务逻辑：解析Token获取Claims → 比较过期时间与当前时间
     * 异常场景：Token解析失败（格式错误、签名无效等）返回true（视为已过期），不抛出
     *
     * @param token JWT Token字符串（必填）
     * @return true表示已过期或无效，false表示未过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseJWT(secretKey, token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 创建JWT Token（静态方法）
     * 业务逻辑：使用HS256签名算法 → 计算过期时间（当前时间+ttlMillis） → 构建JWT body（claims + 签名 + 过期时间） → 压缩成字符串
     * 注意：claims中的私有声明必须在setClaims中设置，若在标准声明之后设置会覆盖
     *
     * @param secretKey 签名密钥（必填，32位以上字符串）
     * @param ttlMillis Token有效期（毫秒）
     * @param claims    自定义声明Map
     * @return 紧凑格式的JWT字符串
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 设置jwt的body
        io.jsonwebtoken.JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * 解析JWT Token（静态方法）
     * 业务逻辑：构建JwtParser → 设置签名密钥 → 解析传入的Token → 返回Claims body
     * 异常场景：Token格式错误、签名不匹配、Token已过期时抛出JwtException等异常，由调用方处理
     *
     * @param secretKey 签名密钥（必填，必须与生成时一致）
     * @param token     JWT Token字符串（必填）
     * @return Token中包含的Claims声明集合
     * @throws io.jsonwebtoken.JwtException 签名无效或Token格式错误时抛出
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 得到DefaultJwtParser
        Claims claims = Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                // 构建JwtParser
                .build()
                // 设置需要解析的jwt
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
}