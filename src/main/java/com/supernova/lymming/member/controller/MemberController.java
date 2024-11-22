package com.supernova.lymming.member.controller;

import com.supernova.lymming.member.dto.MemberInfoDto;
import com.supernova.lymming.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 모든 사용자 리스트 반환
    @GetMapping("/member/list")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<MemberInfoDto>> getUserList() {
        // 여러 사용자 정보 반환
        List<MemberInfoDto> memberInfoDtoList = memberService.getUserList();
        return ResponseEntity.ok(memberInfoDtoList);
    }

    // 특정 사용자 상세 정보 반환
    @GetMapping("/member/list/{userId}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<MemberInfoDto> getUserInfoDetail(@PathVariable Long userId) {
        // 특정 사용자에 대한 정보 반환
        MemberInfoDto memberInfoDto = memberService.getUserInfoByUserId(userId);
        return ResponseEntity.ok(memberInfoDto);
    }

    // 닉네임 중복확인
    @GetMapping("/member/check-nickname")
    public ResponseEntity checkUserNickname(@RequestParam String nickname){
        boolean isExist = memberService
    }
}