package com.supernova.lymming.github.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supernova.lymming.jwt.JwtTokenProvider;
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
        log.info("redirectUri는 : {}", redirectUri);
        log.info("인증 성공: 사용자 {} - 실행 위치: {}", authentication.getName(), getExecutionLocation());

        String targetUrl = deterMineTargetUrl(request, response, authentication);
        // deterMineTargetUrl 메소드를 호출 해서 사용자가 인증에 성공한 후 사용자가 이동하게 될 최종 URL
        // 이 url은 인증 다음단계로 이동하는데 사용된다.
        // 즉, 인증에 성공했다는 뜻

        log.info("리디렉션할 대상 URL: {} - 실행 위치: {}", targetUrl, getExecutionLocation());

        if (response.isCommitted()) {
            log.warn("응답이 이미 커밋되었습니다. 리디렉션할 수 없습니다. - 실행 위치: {}", getExecutionLocation());
            return;
        }
        clearAuthenticationAttributes(request, response);
        // 인증에 성공했다면 민감 정보는 삭제한다.
        // 쿠키 포함

        //인증
        if (response.getStatus() != HttpServletResponse.SC_BAD_REQUEST) {
            log.info("리디렉션할 URL: {} - 실행 위치: {}", targetUrl, getExecutionLocation());
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }

        // 응답 코드가 bad_request가 아니면 getRedirecttStrategy의
        // sendRedirect를 호출해 targetUrl로 리다이렉트

        //결론은 리다이렉트 할 URL에 엑세스 토큰을 추가해서 반환
    }

    public String deterMineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        try {
            log.info("deterMineTargetUrl 메소드 호출됨.");
            log.info("대상 URL 결정 중... - 실행 위치: {}", getExecutionLocation());

            // 쿠키에서 리디렉션 URI를 가져오기
            Optional<String> redirect = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                    .map(Cookie::getValue);

            ObjectMapper objectMapper = new ObjectMapper();

            // 리디렉션 URI 검증
            if (redirect.isPresent() && !isAuthorizedRedirectUri(redirect.get())) {
                log.error("리디렉션 URI가 허용되지 않음: {} - 실행 위치: {}", redirect.get(), getExecutionLocation());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                objectMapper.writeValue(response.getWriter(), new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "redirect_uri가 일치하지 않습니다."));
                return null; // 에러 발생 시 null 반환
            }

            // 최종 리디렉션 URI 결정
            String targetUrl = redirect.orElse(getDefaultTargetUrl());
            log.info("결정된 리디렉션 URI: {} - 실행 위치: {}", targetUrl, getExecutionLocation());

            // JWT 생성
            log.info("사용자 {}에 대한 액세스 토큰 생성 중... - 실행 위치: {}", authentication.getName(), getExecutionLocation());
            String accessToken = tokenProvider.createAccessToken(authentication);

            // 리프레시 토큰 생성
            tokenProvider.createRefreshToken(authentication, response);
            log.info("리프레시 토큰 생성 완료: {} - 실행 위치: {}", accessToken, getExecutionLocation());

            // 엑세스 토큰을 포함한 최종 리다이렉트 URL 반환
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("accessToken", accessToken)
                    .build().toUriString();

        } catch (Exception e) {
            log.error("deterMineTargetUrl 메소드에서 예외 발생: {}", e.getMessage());
            return null; // 예외 발생 시 null 반환
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        // 인증이 성공적으로 이루어진 후 사용자의 정보를 정리하는데 사용된다.

        log.info("인증 속성 제거 중... - 실행 위치: {}", getExecutionLocation());
        super.clearAuthenticationAttributes(request);
        // 상위 메소드를 호출해서 기본 클래스에서 정의 된 인증 속성 제거 로직을 수행한다.
        // 세션에서 사용자 관련 인증 정보를 제거한다. (Oauth2는 세션에다가 저장해두기 때문에)

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        log.info("인증 속성 제거 완료. - 실행 위치: {}", getExecutionLocation());
        // 요청 및 응답에 포함된 인증 요청 관련 쿠키도 삭제한다.
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        log.info("리디렉션 URI가 허용되는지 확인 중: {} - 실행 위치: {}", uri, getExecutionLocation());
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUri);

        // 추가된 로그
        log.info("authorizedUri: {}, clientRedirectUri: {}", authorizedUri, clientRedirectUri);

        boolean isAuthorized = authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort();
        log.info("허용된 리디렉션 URI 여부: {} - 실행 위치: {}", isAuthorized, getExecutionLocation());
        return isAuthorized;
    }

    private String getExecutionLocation() {
        // 현재 메소드의 실행위치를 문자열로 반환한다.
        StackTraceElement element = Thread.currentThread().getStackTrace()[2];
        return String.format("%s.%s() (파일: %s, 행: %d)",
                element.getClassName(),
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber());
    }
}