package com.supernova.lymming.kakao.controller;

import com.supernova.lymming.kakao.dto.LoginResponse;
import com.supernova.lymming.kakao.service.KakaoService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
public class KakaoUserController {

    private final KakaoService kakaoService;

    @PostMapping("/api/kakao/login")
    @ApiOperation(value = "카카오 로그인", notes = "카카오로 로그인 시 실행되는 API")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestBody Map<String, String> requestBody, HttpServletRequest request){
        String code = requestBody.get("code");  // 클라이언트에서 보낸 "code" 값 추출
//        System.out.println("Received code: " + code);
        try {
            // 현재 도메인 확인
            String currentDomain = request.getServerName();
            return ResponseEntity.ok(kakaoService.kakaoLogin(code,currentDomain));
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"item not found");
        }
    }

}