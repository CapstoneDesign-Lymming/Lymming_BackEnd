package com.supernova.lymming.github.controller;

import com.supernova.lymming.github.dto.GithubUser;
import com.supernova.lymming.github.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // GitHub 액세스 토큰을 사용하여 JWT 생성
    @PostMapping("/login/github")
    public ResponseEntity<?> loginWithGithub(@RequestBody Map<String, String> request) {
        log.info("GitHub login request received.");

        String accessToken = request.get("accessToken");

        // Access Token 유효성 검사 및 사용자 정보 가져오기
        try {
            // GitHub API를 통해 Access Token 유효성 검사
            ResponseEntity<?> githubResponse = authService.validateToken(accessToken);
            if (githubResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token이 유효하지 않습니다.");
            }

            // Access Token 유효한 경우, 사용자 정보 획득
            Map<String, Object> userInfo = authService.getUserInfo(accessToken);

            // 사용자 정보를 DTO로 매핑
            GithubUser githubUserDto = authService.getGitHubUser(accessToken);

            // JWT 생성
            String jwt = authService.createJwt(userInfo);

            // 사용자 정보와 JWT를 응답으로 반환
            return ResponseEntity.ok(Map.of("jwt", jwt, "user", githubUserDto));
        } catch (Exception e) {
            log.error("Error during GitHub login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Refresh token request received.");

        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더 확인
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Access token이 제공되지 않았습니다.");
        }

        // Access Token 추출
        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            return ResponseEntity.badRequest().body("Access token이 제공되지 않았습니다.");
        }

        try {
            // GitHub API를 통해 Access Token 유효성 검사
            ResponseEntity<?> githubResponse = authService.validateToken(token);
            if (githubResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token이 유효하지 않습니다.");
            }

            // Access Token 유효한 경우, 사용자 정보 획득
            Map<String, Object> userInfo = authService.getUserInfo(token);

            // 사용자 정보를 바탕으로 JWT 생성
            String jwt = authService.createJwt(userInfo);

            // JWT를 응답으로 반환
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token 갱신 중 오류가 발생했습니다.");
        }
    }
}
