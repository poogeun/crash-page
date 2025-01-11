package com.fastcampus.crash.config;

import com.fastcampus.crash.model.user.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebConfiguration {

  @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;
  @Autowired private JwtExceptionFilter jwtExceptionFilter;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        List.of("http://localhost:3000", "http://127.0.0.1:3000")); // 프론트 연동 위함
    configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE"));
    configuration.setAllowedHeaders(List.of("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/v1/**", configuration); // 위의 cors 패턴을 특정 url 에 적용
    return source;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors(Customizer.withDefaults())
        .authorizeHttpRequests(
            (requests) ->
                requests
                    .requestMatchers(HttpMethod.POST, "/api/*/users", "/api/*/users/authenticate")
                    .permitAll()
                    .requestMatchers(
                        HttpMethod.GET, "/api/*/session-speakers", "/api/*/session-speakers/**")
                    .permitAll()
                    .requestMatchers("/api/*/session-speakers", "/api/*/session-speakers/**")
                    .hasAuthority(Role.ADMIN.name())
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .csrf(CsrfConfigurer::disable)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, jwtAuthenticationFilter.getClass())
        .httpBasic(HttpBasicConfigurer::disable);

    return http.build();
  }
}
