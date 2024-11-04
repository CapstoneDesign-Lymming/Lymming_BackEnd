package com.supernova.lymming.github.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supernova.lymming.github.jwt.JwtTokenProvider;
import com.supernova.lymming.github.repository.CookieAuthorizationRequestRepository;
import com.supernova.lymming.github.util.CookieUtil;
import com.supernova.lymming.github.util.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.supernova.lymming.github.repository.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
@Log4j2
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;
    private final JwtTokenProvider tokenProvider;
    private final CookieAuthorizationRequestRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("인증 성공: 사용자 {} - 실행 위치: {}", authentication.getName(), getExecutionLocation());
        String targetUrl = deterMineTargetUrl(request, response, authentication);
        log.info("리디렉션할 대상 URL: {} - 실행 위치: {}", targetUrl, getExecutionLocation());

        if (response.isCommitted()) {
            log.warn("응답이 이미 커밋되었습니다. 리디렉션할 수 없습니다. - 실행 위치: {}", getExecutionLocation());
            return;
        }
        clearAuthenticationAttributes(request, response);
        if (response.getStatus() != HttpServletResponse.SC_BAD_REQUEST) {
            log.info("리디렉션할 URL: {} - 실행 위치: {}", targetUrl, getExecutionLocation());
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }

    protected String deterMineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("대상 URL 결정 중... - 실행 위치: {}", getExecutionLocation());
        Optional<String> redirect = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        ObjectMapper objectMapper = new ObjectMapper();

        if (redirect.isPresent() && !isAuthorizedRedirectUri(redirect.get())) {
            log.error("리디렉션 URI가 허용되지 않음: {} - 실행 위치: {}", redirect.get(), getExecutionLocation());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "redirect_uri가 일치하지 않습니다."));
            return null; // Early return in case of error
        }

        String targetUrl = redirect.orElse(getDefaultTargetUrl());
        log.info("사용할 대상 URL: {} - 실행 위치: {}", targetUrl, getExecutionLocation());

        //JWT 생성
        log.info("사용자 {}에 대한 액세스 토큰 생성 중... - 실행 위치: {}", authentication.getName(), getExecutionLocation());
        String accessToken = tokenProvider.createAccessToken(authentication);
        tokenProvider.createRefreshToken(authentication, response);
        log.info("액세스 토큰 생성 완료: {} - 실행 위치: {}", accessToken, getExecutionLocation());

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("accessToken", accessToken)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        log.info("인증 속성 제거 중... - 실행 위치: {}", getExecutionLocation());
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        log.info("인증 속성 제거 완료. - 실행 위치: {}", getExecutionLocation());
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        log.info("리디렉션 URI가 허용되는지 확인 중: {} - 실행 위치: {}", uri, getExecutionLocation());
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUri);

        boolean isAuthorized = authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort();
        log.info("허용된 리디렉션 URI 여부: {} - 실행 위치: {}", isAuthorized, getExecutionLocation());
        return isAuthorized;
    }

    private String getExecutionLocation() {
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        return String.format("%s.%s() (파일: %s, 행: %d)",
                element.getClassName(),
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber());
    }
}
