package com.supernova.lymming.github.controller;

import com.supernova.lymming.github.auth.CustomOAuthUserService;
import com.supernova.lymming.github.auth.CustomUserDetails;
import com.supernova.lymming.github.auth.GithubOAuth2UserInfo;
import com.supernova.lymming.github.dto.SignupDto;
import com.supernova.lymming.github.entity.Gender;
import com.supernova.lymming.github.entity.LoginType;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
public class GithubUserController {

    private final UserRepository userRepository;

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

        System.out.println(refreshToken+"토큰");

        User existingUser = userRepository.findByServerNickname(refreshToken)
                .orElseThrow(()->new IllegalStateException("등록된유저가 아닙니다"));


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
            log.info("UserImg은: {}", existingUser.getStack().toString());
        }

        if (userUpdateDto.getGender() != null) {
            existingUser.setGender(Gender.valueOf(String.valueOf(userUpdateDto.getGender())));
            log.info("Gender은 : {}", existingUser.getGender());
        }
        if (userUpdateDto.getJob() != null) {
            existingUser.setJob(userUpdateDto.getJob());
            log.info("직업은 : {}", existingUser.getJob());
        }
        if (userUpdateDto.getBio() != null) {
            existingUser.setBio(userUpdateDto.getBio());
            log.info("한줄소개는 : {}", existingUser.getBio());
        }
        if (userUpdateDto.getFavorites() != null) {
            existingUser.setFavorites(userUpdateDto.getFavorites());
        }
        if (userUpdateDto.getInterests() != null) {
            existingUser.setInterests(userUpdateDto.getInterests().toString());
        }
        if (userUpdateDto.getDevStyle() != null) {
            existingUser.setDevStyle(userUpdateDto.getDevStyle());
            log.info("개발 스타일은 : {}", existingUser.getDevStyle());
        }

        User updatedUser = userRepository.save(existingUser); // 변경사항 저장

        return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환


    }
}