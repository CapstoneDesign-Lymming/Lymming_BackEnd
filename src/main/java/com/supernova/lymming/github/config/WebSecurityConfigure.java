package com.supernova.lymming.github.config;

import com.supernova.lymming.github.auth.CustomOAuthUserService;
import com.supernova.lymming.github.auth.OAuth2AuthenticationFailureHandler;
import com.supernova.lymming.github.auth.OAuth2AuthenticationSuccessHandler;
import com.supernova.lymming.github.jwt.JwtAccessDeniedHandler;
import com.supernova.lymming.github.jwt.JwtAuthenticationEntryPoint;
import com.supernova.lymming.github.jwt.JwtAuthenticationFilter;
import com.supernova.lymming.github.jwt.JwtTokenProvider;
import com.supernova.lymming.github.repository.CookieAuthorizationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Log4j2
public class WebSecurityConfigure {

    private final CustomOAuthUserService customOAuth2UserService;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain...");

        http
                .cors()
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .rememberMe().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        log.info("Disabled default security features: httpBasic, csrf, formLogin, rememberMe");

        // oauth2Login 설정
        http.oauth2Login()
                .authorizationEndpoint().baseUri("/api/login/oauth2/code/github")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                .and()
                .redirectionEndpoint().baseUri("/api/login/oauth2/redirect/github")
                .and()
                .userInfoEndpoint().userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler) // 성공 핸들러 등록
                .failureHandler(oAuth2AuthenticationFailureHandler); // 실패 핸들러 등록

        log.info("OAuth2 login configured with success and failure handlers");

        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);

        log.info("Configured exception handling with custom entry point and access denied handler");

        // JWT 필터 설정
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        log.info("Added JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter");

        return http.build();
    }
}