package com.supernova.lymming.github.service;

import com.supernova.lymming.github.auth.CustomUserDetails;
import com.supernova.lymming.github.jwt.JwtTokenProvider;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.github.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthService {

    @Value("${REFRESH_COOKIE_KEY}")
    private String cookieKey;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public String refreshToken(HttpServletRequest request, HttpServletResponse response, String oldAccessToken) {
        String oldRefreshToken = CookieUtil.getCookie(request, cookieKey)
                .map(Cookie::getValue)
                .orElseThrow(() -> new RuntimeException("refreshToken이 없습니다."));

        if (!jwtTokenProvider.validateToken(oldRefreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다.");
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(oldAccessToken);
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        Long id = (user.getUser().getUserId());

        String savedToken = userRepository.getRefreshToken(id);

        if (!savedToken.equals(oldRefreshToken)) {
            throw new RuntimeException("Refresh Token이 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        jwtTokenProvider.createRefreshToken(authentication, response);

        return accessToken;
    }
}
