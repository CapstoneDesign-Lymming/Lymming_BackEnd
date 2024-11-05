package com.supernova.lymming.kakao.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supernova.lymming.jwt.GithubJwtTokenProvider;
import com.supernova.lymming.kakao.dto.LoginResponse;
import com.supernova.lymming.kakao.entity.KakaoUser;
import com.supernova.lymming.kakao.repository.KakaoUserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import java.util.Collections;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final KakaoUserRepository kakaoUserRepository;
    private final GithubJwtTokenProvider githubJwtTokenProvider;

    @Value("${kakao.key.client-id}")
    // 클라이언트 id 리다이렉트 uri를 환경변수에서 가져오기
    private String clientId;
    @Value("${kakao.key.redirect-url}")
    private String redirectUrl;

    public LoginResponse kakaoLogin(String code, String currentDomain) {

        // 리다이렉트 uri 선택 메소드
        // 근데 테스트 용으로 진행할 것이기 때문에 도메인은 로컬호스트로
        String redirectUrl = "http://localhost:5173/auth";

        // 인가코드 -> 엑세스 토큰 요청
        String accessToken = getAccessToken(code, redirectUrl);

        // 토큰으로 카카오 api 호출해 사용자 정보 받아옴
        Authentication userInfo = getKakaoUserInfo(accessToken);

        // 받아온 정보로 회원가입, 로그인 처리
        LoginResponse kakaoUserResponse = kakaoUserLogin(userInfo);

        return kakaoUserResponse;
    }

    // 받아온 정보로 회원가입, 로그인 처리
    private LoginResponse kakaoUserLogin(Authentication userInfo) {

        OAuth2User oAuth2User = (OAuth2User) userInfo.getPrincipal();
        // 사용자 정보 불러오기
        Long uid = Long.valueOf(oAuth2User.getAttributes().get("uid").toString());
        String nickName = oAuth2User.getAttributes().get("nickname").toString();

        // 닉네임으로 사용자 조회시 사욪가가 db에 있으면 사용자 정보 없으면 null
        KakaoUser kakaoUser = kakaoUserRepository.findByNickname(nickName).orElse(null);


        // 사용자가 있을경우 토큰 생성 없을경우 사용자 추가하고 토큰 생성
        if (kakaoUser == null) {
            kakaoUser = new KakaoUser();
            kakaoUser.setUid(uid);
            kakaoUser.setNickname(nickName);
            kakaoUser.setLoginType("kakao");
            kakaoUserRepository.save(kakaoUser);
        }

        String tokens = githubJwtTokenProvider.createAccessToken(userInfo);
        return new LoginResponse(uid, nickName, tokens);
    }

    // access token을 사용해 사용자 정보를 불러온다
    private Authentication getKakaoUserInfo(String accessToken) {
        HashMap<String, Object> userInfo = new HashMap<String, Object>();


        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();

        userInfo.put("id", id);
        userInfo.put("nickname", nickname);

        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                Collections.singletonMap("nickname", nickname),
                "nickname"
        );

        Authentication authentication = new OAuth2AuthenticationToken(
                oAuth2User,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                "Kakao"
        );

        return authentication;
    }

    // 인가코드를 통해 엑세스 토큰 요청
    private String getAccessToken(String code, String redirectUri) {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        // 서버가 데이터를 url 인코딩 방식으로 처리하도록 지정
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        // 인증타입
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        // restful 서비스를 호출할수 있는 템플릿
        RestTemplate rt = new RestTemplate();
        // http 요청 수행 (post 요청, http헤더 바디를 string 형식으로 리턴 받는다)
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // Http 응답을 access token으로 파싱하기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        // http body에서 access 토큰을 가져와 문자열 형태로 변환 후 반환
        return jsonNode.get("access_token").asText(); //토큰 전송

    }
}
