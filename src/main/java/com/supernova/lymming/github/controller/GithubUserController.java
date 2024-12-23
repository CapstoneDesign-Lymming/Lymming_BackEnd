package com.supernova.lymming.github.controller;

import com.supernova.lymming.github.auth.CustomUserDetails;
import com.supernova.lymming.github.dto.SignupDto;
import com.supernova.lymming.github.entity.Gender;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.github.service.AuthService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class GithubUserController {

    private final UserRepository userRepository;
    private final AuthService authService;

    @GetMapping("/api/auth/current-user")
    @ApiOperation(value = "기존 유저 로그인", notes = "토큰이 저장되어 있는 기존 유저의 로그인 시 실행되는 API")
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
    @ApiOperation(value = "회원가입", notes = "신규 가입자 회원가입 API")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<User> updateUser(@AuthenticationPrincipal CustomUserDetails user,
                                           @RequestBody SignupDto userUpdateDto) {

        String refreshToken = userUpdateDto.getRefreshToken();

//        System.out.println(refreshToken+"토큰");

        User existingUser = userRepository.findByRefreshToken(refreshToken)
                .orElseThrow(()->new IllegalStateException("등록된유저가 아닙니다"));

        if (userUpdateDto.getNickname() != null) {
            existingUser.setNickname(userUpdateDto.getNickname());
        }
        if (userUpdateDto.getStack() != null) {
            List<String> stackList = Arrays.asList(userUpdateDto.getStack().split(", "));
            String stack = String.join(", ", stackList); // 콤마로 구분된 문자열
            existingUser.setStack(stack);
        }
        if (userUpdateDto.getUserImg() != null) {
            existingUser.setUserImg(userUpdateDto.getUserImg().toString());
        }
        if (userUpdateDto.getGender() != null) {
            existingUser.setGender(Gender.valueOf(String.valueOf(userUpdateDto.getGender())));
        }
        if (userUpdateDto.getJob() != null) {
            existingUser.setJob(userUpdateDto.getJob());
        }
        if (userUpdateDto.getDeveloper_type()!=null){
            existingUser.setDeveloper_type(userUpdateDto.getDeveloper_type());
        }
        log.info("Developer Type은 : {}",userUpdateDto.getDeveloper_type());

        if (userUpdateDto.getBio() != null) {
            existingUser.setBio(userUpdateDto.getBio());
        }
        if (userUpdateDto.getFavorites() != null) {
            existingUser.setFavorites(userUpdateDto.getFavorites());
        }
        if (userUpdateDto.getPosition() != null) {
            existingUser.setPosition(userUpdateDto.getPosition().toString());
        }
        if (userUpdateDto.getDevStyle() != null) {
            List<String> devStyleList = Arrays.asList(userUpdateDto.getDevStyle().split(", "));
            String devStyleString = String.join(", ", devStyleList); // 콤마로 구분된 문자열
            existingUser.setDevStyle(devStyleString);
        }
        User updatedUser = userRepository.save(existingUser); // 변경사항 저장

        return ResponseEntity.ok(updatedUser); // 업데이트된 사용자 정보 반환


    }
}