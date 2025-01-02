package com.fastcampus.crash.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

  private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

  private final SecretKey key;

  // 시크릿키를 매번 랜덤으로 생성하지 않기 위해 미리 설정해둔 값으로 초기화
  public JwtService(@Value("${jwt.secret-key}") String key) {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
  }

  // 실제로 jwt 인증에서 사용할 메서드
  public String generateAccessToken(UserDetails userDetails) {
    return generateToken(userDetails.getUsername());
  }

  public String getUsername(String accessToken) {
    return getSubject(accessToken);
  }

  // Jwt 토큰에 담을 subject 정보를 받아서 토큰 생성 - username
  private String generateToken(String subject) {
    var now = new Date(); // 현재시각
    var exp = new Date(now.getTime() + (1000 * 60 * 60 * 3)); // 만료시점 3시간 후
    return Jwts.builder()
        .subject(subject)
        .signWith(key) // Jwt 생성
        .issuedAt(now)
        .expiration(exp)
        .compact();
  }

  // jwt 에서 subject 를 추출 해내는 메서드
  private String getSubject(String token) {
    try {
      return Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getSubject();
    } catch (JwtException exception) {
      logger.error("JwtException", exception);
      throw exception;
    }
  }
}
