package com.supernova.lymming.github.controller;

import com.supernova.lymming.github.auth.CustomOAuthUserService;
import com.supernova.lymming.github.auth.CustomUserDetails;
import com.supernova.lymming.github.auth.GithubOAuth2UserInfo;
import com.supernova.lymming.github.dto.SignupDto;
import com.supernova.lymming.github.entity.Gender;
import com.supernova.lymming.github.entity.LoginType;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.github.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Log4j2
@RestController
@RequiredArgsConstructor
public class GithubUserController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/api/auth/current-user")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    // 메소드 진입을 위해 USER 역할 필요
    public User getCurrentUser(@AuthenticationPrincipal CustomUserDetails user) {
        // 현재 인증된 사용자의 정보를 가져온다.
        // CustomUserDetails는 사용자의 정보를 포함하고 있는 클래스

        Long userId = user.getUserId();
        // user 테이블의 getUserID() 메소드를 호출
        return userRepository.findById(userId)
                // userID를 통해 사용자 정보를 조회하고 없으면 예외 발생
                .orElseThrow(() -> new IllegalStateException("등록된 유저가 아닙니다."));
    }

    @PutMapping("/api/auth/sign-up")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal CustomUserDetails user,
                                           @RequestBody SignupDto userUpdateDto) {

        log.info("회원가입 메소드 들어옴");

        String refreshToken = userUpdateDto.getRefreshToken();
        log.info("회원가입 사용자의 refreshToken: {}", refreshToken);

        System.out.println(refreshToken + "토큰");

        Map<String, Object> userInfo = authService.getUserInfo(refreshToken);
        log.info("userInfo는 : {}", userInfo);

        String serverNickname = (String) userInfo.get("login");
        log.info("UserInfo에서 할당된 serverNickname: {}", serverNickname);

        // 기존 사용자의 serverNickname만 조회
        Optional<User> existingUserOptional = userRepository.findByServerNickname(serverNickname);
        log.info("기존 사용자의 닉네임 조회 : {}", existingUserOptional);

        if (existingUserOptional.isPresent()) {
            log.info("사용자가 이미 존재하는 메소드 들어옴");
            // 기존 사용자라면 로그인 처리만 하고 정보 업데이트는 하지 않음
            User existingUser = existingUserOptional.get();
            log.info("이미 회원가입된 사용자: {}", existingUser);

            // 기존 사용자라면 그냥 로그인 처리 (정보 업데이트는 생략)
            return ResponseEntity.ok(existingUser); // 기존 사용자의 정보를 반환

            //return (ResponseEntity<User>) ResponseEntity.ok(); //사용자 정보를 반환하지 않고 HTTP 상태 코드만 반환
        } else {
            // 기존 사용자가 아니라면 신규 사용자로 간주하여 정보 업데이트
            User existingUser = userRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new IllegalStateException("등록된 유저가 아닙니다"));

            // 신규 사용자의 정보 업데이트
            if (userUpdateDto.getNickname() != null) {
                existingUser.setNickname(userUpdateDto.getNickname());
                log.info("NickName은: {}", existingUser.getNickname());
            }
            if (userUpdateDto.getStack() != null) {
                existingUser.setStack(userUpdateDto.getStack().toString());
                log.info("Stack은: {}", existingUser.getStack().toString());
            }
            if (userUpdateDto.getUserImg() != null) {
                existingUser.setUserImg(userUpdateDto.getUserImg().toString());
                log.info("UserImg은: {}", existingUser.getUserImg());
            }

            if (userUpdateDto.getGender() != null) {
                existingUser.setGender(Gender.valueOf(String.valueOf(userUpdateDto.getGender())));
                log.info("Gender은: {}", existingUser.getGender());
            }
            if (userUpdateDto.getJob() != null) {
                existingUser.setJob(userUpdateDto.getJob());
                log.info("직업은: {}", existingUser.getJob());
            }
            if (userUpdateDto.getBio() != null) {
                existingUser.setBio(userUpdateDto.getBio());
                log.info("한줄소개는: {}", existingUser.getBio());
            }
            if (userUpdateDto.getFavorites() != null) {
                existingUser.setFavorites(userUpdateDto.getFavorites());
            }
            if (userUpdateDto.getInterests() != null) {
                existingUser.setInterests(userUpdateDto.getInterests().toString());
            }
            if (userUpdateDto.getDevStyle() != null) {
                existingUser.setDevStyle(userUpdateDto.getDevStyle());
                log.info("개발 스타일은: {}", existingUser.getDevStyle());
            }

            // 신규 사용자 정보를 업데이트하여 저장
            User updatedUser = userRepository.save(existingUser);

            return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환
        }
    }
}