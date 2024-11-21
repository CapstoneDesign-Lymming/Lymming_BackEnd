package com.supernova.lymming.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supernova.lymming.github.dto.GithubUser;
import com.supernova.lymming.github.entity.LoginType;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.util.*;

@Log4j2
@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    private final long EXPIRATION_TIME = 86400000; // JWT 만료 시간 (예: 1일)

    public AuthService(RestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    // 인가 코드를 가지고 GitHub에서 액세스 토큰을 요청
    public String getAccessToken(String code) {

        // GitHub의 OAuth 액세스 토큰 엔드포인트 URL
        String url = "https://github.com/login/oauth/access_token";

        // 요청 헤더 설정: Content-Type 및 Accept 헤더를 JSON으로 지정
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        // 요청 본문 설정: 클라이언트 ID, 클라이언트 시크릿, 인가 코드 포함
        Map<String, String> body = Map.of(
                "client_id", clientId,           // GitHub OAuth 애플리케이션의 클라이언트 ID
                "client_secret", clientSecret,   // GitHub OAuth 애플리케이션의 클라이언트 시크릿
                "code", code                     // 클라이언트로부터 전달받은 인가 코드
        );

        // HttpEntity 객체 생성: 요청 헤더와 본문을 포함
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // GitHub에 POST 요청 전송, 응답을 ResponseEntity로 받음
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        // 응답 상태가 200 OK이고, 응답 본문이 비어 있지 않은 경우 액세스 토큰 추출
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String accessToken = (String) response.getBody().get("access_token"); // 액세스 토큰 추출
            return accessToken; // 액세스 토큰 반환
        }

        // 액세스 토큰을 받지 못한 경우 null 반환
        return null;
    }


    public ResponseEntity<?> validateToken(String token) {
        // 전달된 토큰이 null인 경우 로그 출력
        if (token == null) {
            log.error("전달된 토큰이 null입니다.");
        } else {
            log.info("전달된 토큰: {}", token);
        }

        String url = "https://api.github.com/user"; // GitHub API 엔드포인트

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); // Bearer 접두사 포함

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // GitHub API 호출
        ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response;
    }

    public Map<String, Object> getUserInfo(String token) {

        // GitHub API URL
        String url = "https://api.github.com/user";

        String accessToken = token;

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // Authorization 헤더에서 토큰 추출
        if (headers == null ){
            log.error("headers가 null 입니다.");
        } else if (headers.get("Authorization").equals("Bearer ")) {
            log.error("Authorization 헤더가 잘못된 형식입니다. Bearer 형식이 아닙니다.");
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
        }

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // GitHub API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        // GitHub에서 사용자 정보 반환
        String responseBody = response.getBody();

        // 응답 본문을 JSON으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error parsing the response from GitHub API");
        }

        // GitHub API에서 사용자 정보 처리
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", jsonNode.get("id").asText());
        userInfo.put("login", jsonNode.get("login").asText());
        userInfo.put("name", jsonNode.get("name").asText());
        userInfo.put("email", jsonNode.get("email").asText());

        return userInfo;
    }

    // SecretKey 객체 생성
    private SecretKey getSigningKey() {
        // String 형식의 secretKey를 SecretKey 객체로 변환
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createJwt(Map<String, Object> userInfo) {

        String serverNickname = (String) userInfo.get("login");// GitHub 사용자 이름 또는 고유 ID 등 필요한 정보 추출

        // JWT 생성
        String jwt = Jwts.builder()
                .setSubject(serverNickname) // JWT의 주체 설정
                .setIssuedAt(new Date()) // 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간 설정
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명 알고리즘 및 비밀 키 설정
                .compact(); // JWT 생성

        // refreshToken을 DB에 저장
        saveRefreshTokenToDatabase(jwt,serverNickname);

        return jwt;
    }


    public void saveRefreshTokenToDatabase(String refreshToken, String serverNickname) {
        // 새로운 사용자 객체 생성 또는 기존 사용자 업데이트
        Optional<User> optionalUser = userRepository.findByServerNickname(serverNickname);
        User user = optionalUser.orElse(new User());

        user.setRefreshToken(refreshToken);
        user.setServerNickname(serverNickname);
        user.setLoginType(LoginType.Github); // loginType을 항상 GITHUB으로 설정

        // DB에 저장
        userRepository.save(user);
    }


    public GithubUser getServerNickName(String accessToken) {

        String url = "https://api.github.com/user"; // GitHub API 엔드포인트

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);;

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GithubUser> response = restTemplate.exchange(url, HttpMethod.GET, entity, GithubUser.class);

        GithubUser githubUser = response.getBody();

        return githubUser; // GithubUser 객체를 반환
    }
}