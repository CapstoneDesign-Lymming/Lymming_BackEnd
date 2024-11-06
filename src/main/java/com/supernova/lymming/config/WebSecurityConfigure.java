package com.supernova.lymming.config;

import com.supernova.lymming.github.auth.CustomOAuthUserService;
import com.supernova.lymming.github.auth.OAuth2AuthenticationFailureHandler;
import com.supernova.lymming.github.auth.OAuth2AuthenticationSuccessHandler;
import com.supernova.lymming.jwt.JwtAccessDeniedHandler;
import com.supernova.lymming.jwt.JwtAuthenticationEntryPoint;
import com.supernova.lymming.jwt.JwtAuthenticationFilter;
import com.supernova.lymming.jwt.GithubJwtTokenProvider;
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
    // Security를 설정하기 위한 클래스

    private final CustomOAuthUserService customOAuth2UserService;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final GithubJwtTokenProvider githubJwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 각각 보안 관련 기능을 담당하는 컴포넌트를 주입받기 위해 선언
    // final로 선언해서 생성자를 통해 주입받는다.

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // HTTP 요청을 처리하는 필터 체인을 구성한다.

        log.info("Configuring security filter chain...");

        http
                .cors() // Cors 설정 활성화
                .and()
                .httpBasic().disable() // 기본 인증을 비활성화 .
                .authorizeHttpRequests()
                .antMatchers("/**","https://lymming.link").permitAll()
                .and()
                .csrf().disable() // CSRF 공격 방지를 위한 보호 기능 비활성
                .formLogin().disable() // 기본 제공되는 폼 로그인 기능을 비활성화
                .rememberMe().disable() // 기억하기 기능 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 세션 관리 무상태로 설정해 서버가 클라이언트 세션 유지 하지 않도록 설정

        log.info("Disabled default security features: httpBasic, csrf, formLogin, rememberMe");

        // oauth2 로그인 기능 설정
        http.oauth2Login()

                .authorizationEndpoint().baseUri("/api/login/oauth2/code")
                // 인증 요청이 이뤄지는 엔드포인트
                // 즉, 깃허브 인증을 시작하는 URL로 이 엔드포인트에 들어가면 로그인 페이지로 리다이렉트 되면서 인증 시작

                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                // 사용자 인증 요청을 쿠키에 저장하는 구현체
                // 인증 요청과 관련된 상태 정보를 관리
                .and()

                .redirectionEndpoint().baseUri("/api/login/oauth2/redirect/github")
                //OAuth 인증이 완료된 후 사용자가 리다이렉트 될 엔드포인트의 기본 URI
                // 이 엔드포인트에서 엑세스 토큰 발급
                // 엑세스 토큰을 사용해 사용자 정보 가져올 수 있다.

                .and()
                .userInfoEndpoint().userService(customOAuth2UserService)
                // OAuth2 인증 과정에서 사용자의 정보를 어떻게 가져올지 정의

                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                // OAuth2 로그인 성공 후 처리할 핸들러 설정
                .failureHandler(oAuth2AuthenticationFailureHandler);
        // 실패 후 처리할 핸들러 설정

        log.info("OAuth2 login configured with success and failure handlers");

        //예외처리 설정
        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // 인증에 실패햇을 시 처리할 엔트리 포인트 설정
                .accessDeniedHandler(jwtAccessDeniedHandler);
        // 권한 거부 시 처리할 핸들러 설정

        log.info("Configured exception handling with custom entry point and access denied handler");

        // JWT 필터 추가
        http.addFilterBefore(new JwtAuthenticationFilter(githubJwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        // JwtAuthenticationFilter을 UsernamePasswordAuthenticationFilter 앞에 추가해 JWT를 기반으로 인증 처리

        log.info("Added JwtAuthenticationFilter before UsernamePasswordAuthenticationFilter");

        return http.build();
        // 구성된 HTTP 보안 설정을 바탕으로 SecurittyFilterChain 객체를 빌드해서 반환
    }
}