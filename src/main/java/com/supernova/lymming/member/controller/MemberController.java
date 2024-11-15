package com.supernova.lymming.member.controller;

import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.member.dto.MemberInfoDto;
import com.supernova.lymming.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;

    // 모든 사용자 리스트 반환
    @GetMapping("/member/list")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<MemberInfoDto>> getUserList() {
        log.info("개발자 리스트 메소드 들어옴");

        // 여러 사용자 정보 반환
        List<MemberInfoDto> memberInfoDtoList = memberService.getUserList();
        return ResponseEntity.ok(memberInfoDtoList);
    }

    // 특정 사용자 상세 정보 반환
    @GetMapping("/member/list/{userId}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<MemberInfoDto> getUserInfoDetail(@PathVariable Long userId) {
        log.info("사용자 ID: {}에 대한 정보 요청", userId);

        // 특정 사용자에 대한 정보 반환
        MemberInfoDto memberInfoDto = memberService.getUserInfoByUserId(userId);
        return ResponseEntity.ok(memberInfoDto);
    }
}