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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 유저가 아닙니다."));
    }

    @PutMapping("/users/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal CustomUserDetails user,
                                           @RequestBody SignupDto userUpdateDto) {
        Long userId = user.getUserId();
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("등록된 유저가 아닙니다."));

        // 필드 업데이트 - null이 아닌 경우에만 업데이트
        if (userUpdateDto.getNickname() != null) {
            existingUser.setNickname(userUpdateDto.getNickname());
        }
        if (userUpdateDto.getStack() != null) {
            existingUser.setStack(userUpdateDto.getStack());
        }
        if (userUpdateDto.getGender() != null) {
            existingUser.setGender(Gender.valueOf(String.valueOf(userUpdateDto.getGender())));
        }
        if (userUpdateDto.getJob() != null) {
            existingUser.setJob(userUpdateDto.getJob());
        }
        if (userUpdateDto.getBio() != null) {
            existingUser.setBio(userUpdateDto.getBio());
        }
        if (userUpdateDto.getFavorites() != null) {
            existingUser.setFavorites(userUpdateDto.getFavorites());
        }
        if (userUpdateDto.getInterests() != null) {
            existingUser.setInterests(userUpdateDto.getInterests());
        }
        if (userUpdateDto.getDevStyle() != null) {
            existingUser.setDevStyle(userUpdateDto.getDevStyle());
        }

        User updatedUser = userRepository.save(existingUser); // 변경사항 저장

        return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환
    }
}