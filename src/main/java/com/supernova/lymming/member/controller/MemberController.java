package com.supernova.lymming.member.controller;

import com.supernova.lymming.member.dto.MemberInfoDetailDto;
import com.supernova.lymming.member.dto.MemberInfoDto;
import com.supernova.lymming.member.service.MemberService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "개발자 리스트", notes = "모든 개발자 리스트 반환 시 실행되는 API")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<MemberInfoDto>> getUserList() {
        // 여러 사용자 정보 반환
        List<MemberInfoDto> memberInfoDtoList = memberService.getUserList();
        return ResponseEntity.ok(memberInfoDtoList);
    }

    // 특정 사용자 상세 정보 반환
    @GetMapping("/member/list/{userId}")
    @ApiOperation(value = "특정 개발자 상세 정보 모달", notes = "특정 사용자 상세 정보 반환 시 실행되는 API")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<MemberInfoDetailDto> getUserInfoDetail(@PathVariable Long userId) {
        // 특정 사용자에 대한 정보 반환
        MemberInfoDetailDto memberInfoDetailDto = memberService.getUserInfoByUserId(userId);
        return ResponseEntity.ok(memberInfoDetailDto);
    }

    // 닉네임 중복확인
    @GetMapping("/member/check-nickname")
    @ApiOperation(value = "닉네임 중복확인", notes = "회원가입 시 닉네임 중복 확인시 실행되는 API")
    public ResponseEntity<Boolean> checkUserNickname(@RequestParam String nickname) {
        boolean isExist = memberService.checkNicknameByUserNickname(nickname);

        if (!isExist) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    //개발자 추천 넘겨주기
    @GetMapping("/member/random/list/{userId}")
    @ApiOperation(value = "유형 개발자 리스트", notes = "AI 개발자 유형 일치하는 사용자 정보 반환될 떄 실행되는 API, Token 필요")
    public ResponseEntity<List<MemberInfoDto>> getRandomUserList(@PathVariable Long userId) {
        List<MemberInfoDto> randomMember = memberService.getRandomUsersByDeveloperType(userId);
        return ResponseEntity.ok(randomMember);
    }
}