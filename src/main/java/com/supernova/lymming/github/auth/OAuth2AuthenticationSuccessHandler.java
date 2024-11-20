package com.supernova.lymming.github.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supernova.lymming.github.repository.CookieAuthorizationRequestRepository;
import com.supernova.lymming.github.util.CookieUtil;
import com.supernova.lymming.github.util.ErrorResponse;
import com.supernova.lymming.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

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
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;
    private final JwtTokenProvider tokenProvider;
    private final CookieAuthorizationRequestRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        String targetUrl = deterMineTargetUrl(request, response, authentication);
        // deterMineTargetUrl 메소드를 호출 해서 사용자가 인증에 성공한 후 사용자가 이동하게 될 최종 URL
        // 이 url은 인증 다음단계로 이동하는데 사용된다.
        // 즉, 인증에 성공했다는 뜻


        if (response.isCommitted()) {
            return;
        }
        clearAuthenticationAttributes(request, response);
        // 인증에 성공했다면 민감 정보는 삭제한다.
        // 쿠키 포함

        //인증
        if (response.getStatus() != HttpServletResponse.SC_BAD_REQUEST) {
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }

        // 응답 코드가 bad_request가 아니면 getRedirecttStrategy의
        // sendRedirect를 호출해 targetUrl로 리다이렉트

        //결론은 리다이렉트 할 URL에 엑세스 토큰을 추가해서 반환
    }

    protected String deterMineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        //사용자가 인증에 성공한 다음에 리다이렉트 할 url을 결정하는 메소드
        // request는 클라이언트의 요청 정보를 담고있는 객체
        // response는 서버의 응답 정보를 담고있는 객체
        // authentication은 인증이 완료된 사용자의 정보 (Security에서 인증 정보를 나타내는)를 포함하고 있는 객체

        //쿠키에서 특정한 정보를  가져오는 메소드로 리다이렉트 URI를 가져오는데 사용된다.

        Optional<String> redirect = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        // CookieUtil의 getCookie 메소드를 사용해서 쿠키에서 리다이렉트 URI를 가져온다.
        // 만약에 쿠키가 존재하면 쿠키 값을 가져와서 Opstional 형태로 저장한다.


        ObjectMapper objectMapper = new ObjectMapper();

        if (redirect.isPresent() && !isAuthorizedRedirectUri(redirect.get())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(response.getWriter(), new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "redirect_uri가 일치하지 않습니다."));
            return null; // Early return in case of error
        }

        String targetUrl = redirect.orElse(getDefaultTargetUrl());
        // 쿠키에서 가져온 URI로 쿠키에 유요한 URI가 존재하면 그걸 사용하고 아니면 기본 리다이렉트 URI 사용

        //JWT 생성
        String accessToken = tokenProvider.createAccessToken(authentication);
        // tokenProvider을 사용해서 사용자 인증 정보를 바탕으로  엑세스 토큰을 생성한다.


        tokenProvider.createRefreshToken(authentication, response);
        // 인증 정보를 바탕으로 리프레시 토큰도 생성해서 응답에 추가한다.

        return "https://lymming.link/participate";
        // 엑세스 토큰을 포함한 최종 리다이렉트 URL을 반환한다.
        // 어디로? 이 메소드를 실행한 곳으로
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        // 인증이 성공적으로 이루어진 후 사용자의 정보를 정리하는데 사용된다.

        super.clearAuthenticationAttributes(request);
        // 상위 메소드를 호출해서 기본 클래스에서 정의 된 인증 속성 제거 로직을 수행한다.
        // 세션에서 사용자 관련 인증 정보를 제거한다. (Oauth2는 세션에다가 저장해두기 때문에)

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        // 요청 및 응답에 포함된 인증 요청 관련 쿠키도 삭제한다.
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUri);

        boolean isAuthorized = authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort();
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