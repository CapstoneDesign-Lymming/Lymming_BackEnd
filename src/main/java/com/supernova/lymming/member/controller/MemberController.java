package com.supernova.lymming.member.controller;

import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/user")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<User> user(@RequestParam String nickname) {

        try {
            User user = memberService.getMemberByNickname(nickname);

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
