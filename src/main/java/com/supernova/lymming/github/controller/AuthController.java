package com.supernova.lymming.github.controller;

import com.supernova.lymming.github.auth.CustomOAuthUserService;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.github.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final CustomOAuthUserService customOAuthUserService;
    private final UserRepository userRepository;

    @PostMapping("/api/login/code/github")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<?> loginWithGithub(@RequestBody Map<String, String> request) {
        log.info("GitHub 로그인 요청 수신");

        String code = request.get("code");
        log.info("인가 코드 수신: {}", code);
        // 요청 본문에서 인가 코드를 추출합니다.

        // GitHub에 인가 코드를 사용해 액세스 토큰 요청
        String accessToken;
        try {
            log.info("인가 코드를 사용하여 GitHub에서 액세스 토큰 요청 시작");
            accessToken = authService.getAccessToken(code); // 인가 코드를 사용해 액세스 토큰을 요청

            log.info("액세스 토큰 획득 성공: {}", accessToken);

        } catch (Exception e) {
            log.error("액세스 토큰 요청 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token 요청에 실패했습니다.");
        }

        // Access Token 유효성 검사 및 사용자 정보 가져오기
        try {
            log.info("액세스 토큰 유효성 검사 시작");
            ResponseEntity<?> githubResponse = authService.validateToken(accessToken);
            log.info("액세스 토큰 유효성 검사 결과 상태 코드: {}", githubResponse.getStatusCode());

            if (githubResponse.getStatusCode() != HttpStatus.OK) {
                log.warn("유효하지 않은 액세스 토큰입니다");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token이 유효하지 않습니다.");
            }

            log.info("액세스 토큰 유효성 검사 성공, 사용자 정보 요청 시작");

            Map<String, Object> userInfo = authService.getUserInfo(accessToken);
            log.info("사용자 정보 획득 성공: {}", userInfo);

            String githubId = userInfo.get("login").toString(); // GitHub의 login 정보 (githubId와 같은 역할)
            log.info("GitHub에서 가져온 githubId: {}", githubId);

            // JWT 토큰 생성
            String jwt = authService.createJwt(userInfo);
            log.info("JWT 토큰 생성 완료: {}", jwt);

            // DB에서 serverNickname으로 사용자 조회
            Optional<User> optionalUser = userRepository.findByServerNickname(githubId);

            if (optionalUser.isPresent()) {
                log.info("기존 사용자 로그인 성공: {}", optionalUser.get().getServerNickname());

                return ResponseEntity.ok(Map.of("jwt", jwt, "user", optionalUser.get()));
            } else {
                log.warn("존재하지 않는 사용자입니다. 로그인 실패.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("등록된 사용자 정보가 없습니다.");
            }

        } catch (Exception e) {
            log.error("GitHub 로그인 과정에서 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그인 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/api/refresh")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    // 토큰 갱신 요청을 처리하는 앤드포인트
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // HTTP 요청과 응답을 처리하는 메소드

        log.info("Refresh token request received.");

        String authorizationHeader = request.getHeader("Authorization");
        // 클라이언트가 요청한 헤어데 포함한 AUthorization 헤더 값을 가져온다
        // 헤더에는 엑세스 토큰이 포함되어있다.
        // 여기서 엑세스 토큰은 깃허브 OAuth2에서 발급한 엑세스 토큰으로 사용자의 권한을 증명한
        // 이 엑세스 토큰을 가지고 서버에서 JWT 엑세스 토큰을 생성하기 위해 필요하다.

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            // Authorization 헤더가 존재하는지 확인하고 Bearer로 시작하는지 확인
            // 조건이 만족하지 않으면 엑세스 토큰은 발급되지 않는다.
            return ResponseEntity.badRequest().body("Access token이 제공되지 않았습니다.");
        }

        String token = authorizationHeader.substring(7).trim();
        // Bearer 문자열 다음에 오는 실제 토큰 값을 추출

        if (token.isEmpty()) {
            return ResponseEntity.badRequest().body("Access token이 제공되지 않았습니다.");
        }

        try {
            ResponseEntity<?> githubResponse = authService.validateToken(token);
            // authService를 통해 깃허브 API에 요청해 엑세스 토큰 유효성을 검사
            if (githubResponse.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token이 유효하지 않습니다.");
            }

            Map<String, Object> userInfo = authService.getUserInfo(token);
            //엑세스 토큰이 유효하면 사용자 정보를 가져오기 위해 userInfo 메소드 호출

            String jwt = authService.createJwt(userInfo);
            // 사용자 정보를 가지고 JWT 토큰 생성을 위해 createJwt 호출
            // 이렇게 생성된 JWT 토큰을 클라이언트의 후속 요청을 인증하는데 사용

            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            log.error("Error during token refresh: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Token 갱신 중 오류가 발생했습니다.");
        }
    }
}