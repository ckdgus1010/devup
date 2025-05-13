package com.upstage.devup.auth.config.jwt;

import com.upstage.devup.auth.config.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * JWT 토큰 생성
     *
     * @param userId 유저 고유 번호
     * @return
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiredDate = new Date(System.currentTimeMillis() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param token 검증할 JWT 토큰
     * @return true: 유효한 토큰, false: 유효하지 않은 토큰
     */
    public boolean validateJwtToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * JWT 토큰에서 유저 ID 추출
     *
     * @param token 유저 ID를 추출할 JWT 토큰
     * @return 추출된 유저 ID
     */
    public long getUserIdFromJwtToken(String token) {
        try {
            Claims claims = parseToken(token);
            return Long.parseLong(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("User Id를 찾을 수 없음: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * JWT 토큰에서 사용자 정보 추출
     *
     * @param token 사용자 정보를 추출할 JWT 토큰
     * @return 추출된 사용자 정보
     */
    public AuthenticatedUser getAuthenticatedUserFromJwtToken(String token) {
        try {
            Claims claims = parseToken(token);
            Long userId = Long.parseLong(claims.getSubject());
            return new AuthenticatedUser(userId);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("토큰에서 사용자 정보를 찾을 수 없음: {}", e.getMessage());
            return null;
        }
    }

    /**
     * JWT 토큰에서 Payload 추출
     *
     * @param token Payload를 추출할 JWT 토큰
     * @return 추출된 내용
     */
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
