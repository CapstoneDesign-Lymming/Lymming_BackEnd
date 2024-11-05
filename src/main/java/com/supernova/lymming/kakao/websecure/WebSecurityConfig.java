package com.supernova.lymming.kakao.websecure;

import com.supernova.lymming.jwt.GithubJwtTokenProvider;
import com.supernova.lymming.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

    private final GithubJwtTokenProvider githubJwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                // 세션 사용 안함
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 회원가입, 로그인 관련 API는 Jwt 인증 없이 접근 가능
                .requestMatchers(new AntPathRequestMatcher("/api/kakao/login")).permitAll()

                .requestMatchers(new AntPathRequestMatcher("/error")).permitAll()

                // 나머지 모든 API는 Jwt 인증 필요
                //.anyRequest().authenticated()
                .and()
                // Http 요청에 대한 Jwt 유효성 선 검사
                .addFilterBefore(new JwtAuthenticationFilter(githubJwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedOrigin("http://localhost:5173"); // 클라이언트 도메인 허용
        configuration.addAllowedOrigin("http://localhost:5174"); // 클라이언트 도메인 허용
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
