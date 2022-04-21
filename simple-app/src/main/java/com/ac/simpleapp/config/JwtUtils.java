package com.ac.simpleapp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    //私钥
    private static final String SECRET_KEY = "demo";

    // 过期时间 毫秒,设置默认半小时; 刷新token过期时间一周；
    private static final long EXPIRATION_TIME = 60 * 30 * 1000;
    private static final long REFRESH_EXPIRATION_TIME = 60 * 60 * 1000 * 24 * 1;

    /**
     * 生成令牌
     *
     * @param userDetails 用户
     * @return 令牌
     */
    public JwtPair generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(Claims.SUBJECT, userDetails.getUsername());
        claims.put(Claims.ISSUED_AT, new Date());
        return generateToken(claims);
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameForToken(String token) {
        try {
            return getClaimsForToken(token).getSubject();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isExpired(String token) throws Exception{
        try {
            Claims claims = getClaimsForToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            new Throwable(e);
        }
        return true;
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public JwtPair refreshToken(String token) {
        try {
            Claims claims = getClaimsForToken(token);
            claims.put(Claims.ISSUED_AT, new Date());
            return generateToken(claims);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 验证令牌
     *
     * @param token       令牌
     * @param userDetails 用户
     * @return 是否有效
     */
    public Boolean validateToken(String token, UserDetails userDetails) throws Exception {
        Claims claims = getClaimsForToken(token);
        return (claims.getSubject().equals(userDetails.getUsername()) && !isExpired(token));
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private JwtPair generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis()+ EXPIRATION_TIME);
        Date refreshExpirationDate = new Date(System.currentTimeMillis()+ REFRESH_EXPIRATION_TIME);
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setExpiration(refreshExpirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
        return new JwtPair(accessToken, refreshToken);
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsForToken(String token) throws Exception {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            new Throwable(e);
        }
        return claims;
    }
}
