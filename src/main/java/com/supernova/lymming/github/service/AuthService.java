package com.supernova.lymming.github.service;

import com.supernova.lymming.github.dto.GithubUser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Map;

@Service
public class AuthService {

    private final RestTemplate restTemplate;

    @Value("${custom.jwt.secretKey}") // 적절한 비밀 키 사용
    private String secretKey;

    private final long EXPIRATION_TIME = 86400000; // JWT 만료 시간 (예: 1일)

    public AuthService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> validateToken(String token) {
        String url = "https://api.github.com/user"; // GitHub API 엔드포인트

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public Map<String, Object> getUserInfo(String token) {
        String url = "https://api.github.com/user"; // GitHub API 엔드포인트

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        // GitHub에서 사용자 정보를 반환
        return response.getBody();
    }

    public String createJwt(Map<String, Object> userInfo) {
        String username = (String) userInfo.get("login"); // GitHub 사용자 이름 또는 고유 ID 등 필요한 정보 추출
        // 추가적인 사용자 정보 처리

        return Jwts.builder()
                .setSubject(username) // JWT의 주체 설정
                .setIssuedAt(new Date()) // 발급 시간 설정
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // 서명 알고리즘 및 비밀 키 설정
                .compact(); // JWT 생성
    }

    public GithubUser getServerNickName(String accessToken) {
        String url = "https://api.github.com/user"; // GitHub API 엔드포인트

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<GithubUser> response = restTemplate.exchange(url, HttpMethod.GET, entity, GithubUser.class);

        return response.getBody(); // GithubUser 객체를 반환
    }
}