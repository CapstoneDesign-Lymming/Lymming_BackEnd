package com.supernova.lymming.mypage.controller;

import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.mypage.dto.MypageDto;
import com.supernova.lymming.mypage.service.MypageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MypageController {
    private final MypageService mypageService;
    private final UserRepository userRepository;

    public MypageController(MypageService mypageService, UserRepository userRepository) {
        this.mypageService = mypageService;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/mypage/{userId}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<MypageDto> showMypage(@PathVariable Long userId) {
        MypageDto mypageDto = mypageService.findUser(userId);
        return ResponseEntity.ok(mypageDto);
    }

    @PutMapping("/api/mypage/{userId}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<MypageDto> updateMypage(
            @PathVariable Long userId,
            @RequestBody MypageDto mypageDto) {
        MypageDto updatedMypageDto = mypageService.updateUser(userId, mypageDto);
        return ResponseEntity.ok(updatedMypageDto);
    }
}