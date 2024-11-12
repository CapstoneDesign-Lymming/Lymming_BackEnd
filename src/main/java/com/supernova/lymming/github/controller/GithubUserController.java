package com.supernova.lymming.github.controller;

import com.supernova.lymming.github.auth.CustomUserDetails;
import com.supernova.lymming.github.dto.SignupDto;
import com.supernova.lymming.github.entity.Gender;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        // 현재 인증된 사용자의 정보를 CustomUserDetails 객체로 주입받아 사용자 정보에 접근 가능

//        Long userId = user.getUserId();\
//        // 현재 인증 된 사용자의 ID를 가져온다.
//
//        User existingUser = userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalStateException("등록된 유저가 아닙니다."));


        String refreshToken = userUpdateDto.getRefreshToken();

        System.out.println(refreshToken+"토큰");

        User existingUser = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalStateException("등록된유저가 아닙니다"));

        // userID를 사용해 DB에서 기존 사용자인지 검사한다,

        // 필드 업데이트 - null이 아닌 경우에만 업데이트
        if (userUpdateDto.getNickname() == null) {
            existingUser.setNickname(userUpdateDto.getNickname());
        }
        if (userUpdateDto.getStack() == null) {
            existingUser.setStack(userUpdateDto.getStack().toString());
        }
        if (userUpdateDto.getGender() == null) {
            existingUser.setGender(Gender.valueOf(String.valueOf(userUpdateDto.getGender())));
        }
        if (userUpdateDto.getJob() == null) {
            existingUser.setJob(userUpdateDto.getJob());
        }
        if (userUpdateDto.getBio() == null) {
            existingUser.setBio(userUpdateDto.getBio());
        }
        if (userUpdateDto.getFavorites() == null) {
            existingUser.setFavorites(userUpdateDto.getFavorites());
        }
        if (userUpdateDto.getInterests() == null) {
            existingUser.setInterests(userUpdateDto.getInterests().toString());
        }
        if (userUpdateDto.getDevStyle() == null) {
            existingUser.setDevStyle(userUpdateDto.getDevStyle());
        }

        User updatedUser = userRepository.save(existingUser); // 변경사항 저장

        return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환
    }
}