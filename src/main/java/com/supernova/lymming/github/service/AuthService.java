package com.supernova.lymming.github.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supernova.lymming.github.dto.GithubUser;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {

    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
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
        log.info("getAccessToken 메소드 들어옴");

        // GitHub의 OAuth 액세스 토큰 엔드포인트 URL
        String url = "https://github.com/login/oauth/access_token";
        log.info("url: " + url);

        // 요청 헤더 설정: Content-Type 및 Accept 헤더를 JSON으로 지정
        HttpHeaders headers = new HttpHeaders();
        log.info("headers 생성");

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        log.info("설정된 headers:{}", headers);

        // 요청 본문 설정: 클라이언트 ID, 클라이언트 시크릿, 인가 코드 포함
        Map<String, String> body = Map.of(
                "client_id", clientId,           // GitHub OAuth 애플리케이션의 클라이언트 ID
                "client_secret", clientSecret,   // GitHub OAuth 애플리케이션의 클라이언트 시크릿
                "code", code                     // 클라이언트로부터 전달받은 인가 코드
        );

        // HttpEntity 객체 생성: 요청 헤더와 본문을 포함
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        log.info("HttpEntity 객체인 requestEntity:{}", requestEntity);

        // GitHub에 POST 요청 전송, 응답을 ResponseEntity로 받음
        ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

        log.info("response:{}", response);

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
        log.info("GitHub API URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); // Bearer 접두사 포함
        log.info("헤더에 Authorization 추가: {}", token);

        log.info("haders는 : {}", headers);


        HttpEntity<String> entity = new HttpEntity<>(headers);
        log.info("HTTP 엔티티 생성 완료");

        // GitHub API 호출
        ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        log.info("GitHub API 응답: {}", response);

        return response;
    }

    public Map<String, Object> getUserInfo(String token) {
        log.info("getUserInfo 메소드 호출, 전달된 토큰: {}", token);

        // GitHub API URL
        String url = "https://api.github.com/user";
        log.info("GitHub API URL: {}", url);

        String accessToken = token;
        log.info("추출된 토큰: {}", accessToken);

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        log.info("header 확인하기:{}", headers);

        // Authorization 헤더에서 토큰 추출
        if (headers == null ){
            log.info("headers가 null 입니다.");
        } else if (headers.get("Authorization").equals("Bearer ")) {
            log.info("Get 한 Authoriztion :{}", headers.get("Authorization"));
            log.error("Authorization 헤더가 잘못된 형식입니다. Bearer 형식이 아닙니다.");
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.");
        }

        log.info("추가된 headers: {}", headers);

        // HTTP 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);
        log.info("HTTP 엔티티 생성 완료");

        // GitHub API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
        log.info("GitHub API 응답: {}", response);

        // GitHub에서 사용자 정보 반환
        String responseBody = response.getBody();
        log.info("GitHub API 응답 본문: {}", responseBody);

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

        log.info("사용자 정보: {}", userInfo);

        return userInfo;
    }

    public String createJwt(Map<String, Object> userInfo) {
        log.info("createJwt 메소드 호출, 사용자 정보: {}", userInfo);

        String username = (String) userInfo.get("login"); // GitHub 사용자 이름 또는 고유 ID 등 필요한 정보 추출
        log.info("사용자 이름: {}", username);

        String jwt = Jwts.builder()
                .setSubject(username) // JWT의 주체 설정
                .setIssuedAt(new Date()) // 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey)// 서명 알고리즘 및 비밀 키 설정
                .compact(); // JWT 생성
        log.info("생성된 JWT: {}", jwt);

        // refreshToken을 DB에 저장
        saveRefreshTokenToDatabase(jwt);

        return jwt;
    }

    public void saveRefreshTokenToDatabase(String refreshToken) {
        // DB에 저장하는 로직
        User token = new User();
        token.setRefreshToken(refreshToken);

        // DB에 저장
        userRepository.save(token);
        log.info("RefreshToken 저장 완료: {}", refreshToken);
    }

    public GithubUser getServerNickName(String accessToken) {
        log.info("getServerNickName 메소드 호출, 전달된 토큰: {}", accessToken);

        String url = "https://api.github.com/user"; // GitHub API 엔드포인트
        log.info("GitHub API URL: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        log.info("헤더에 Authorization 추가: Bearer {}", accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        log.info("HTTP 엔티티 생성 완료");

        ResponseEntity<GithubUser> response = restTemplate.exchange(url, HttpMethod.GET, entity, GithubUser.class);
        log.info("GitHub API 응답: {}", response);

        GithubUser githubUser = response.getBody();
        log.info("GitHub 사용자 정보: {}", githubUser);

        return githubUser; // GithubUser 객체를 반환
    }
}