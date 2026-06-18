package org.example.expert.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // JWT 방식은 서버 세션을 쓰지 않으므로 CSRF 방어가 기본적으로 필요 없음
                .csrf(AbstractHttpConfigurer::disable)
                // 브라우저 기본 로그인 팝업 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // JWT는 세션에 로그인 상태를 저장하지 않으므로 STATELESS 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Spring Security 기본 로그인 폼 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException)
                                -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 필요합니다."))
                        .accessDeniedHandler((request, response, accessDeniedException)
                                -> response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다."))
                )
                // 우리가 만든 JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 배치
                // 즉, 기본 로그인 인증 필터보다 먼저 JWT를 검사하겠다는 의미임
                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
