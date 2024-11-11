package com.supernova.lymming.member.service;

import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;


    public User getMemberByNickname(String nickname) {
        User user = memberRepository.findByNickname(nickname).orElse(null);
        return user;
    }
}
