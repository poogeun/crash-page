package com.fastcampus.crash.config;

import com.fastcampus.crash.service.JwtService;
import com.fastcampus.crash.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired private JwtService jwtService;
  @Autowired private UserService userService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // TODO: JWT 인증 로직
    String BEARER_PREFIX = "Bearer ";
    var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    var securityContext = SecurityContextHolder.getContext();

    // jwt 인증 로직이 이미 수행 되었을 경우 제외
    if (!ObjectUtils.isEmpty(authorization)
        && authorization.startsWith(BEARER_PREFIX)
        && securityContext.getAuthentication() == null) {
      var accessToken = authorization.substring(BEARER_PREFIX.length());
      var username = jwtService.getUsername(accessToken);
      var userDetails = userService.loadUserByUsername(username);

      // 스프링 시큐리티에서 id, password 를 저장하기 위해 사용
      var authenticationToken =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      // 현재 api 정보 포함
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      securityContext.setAuthentication(authenticationToken);
      SecurityContextHolder.setContext(securityContext);
    }

    // 커스텀 필터 다음의 스프링 시큐리티의 다른 필터들도 정상적으로 동작할 수 있게함
    filterChain.doFilter(request, response);
  }
}
