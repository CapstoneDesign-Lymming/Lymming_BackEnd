package com.supernova.lymming.mypage.service;

import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.member.service.MemberService;
import com.supernova.lymming.mypage.dto.MypageDto;
import com.supernova.lymming.mypage.repository.MypageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Log4j2
public class MypageService {

    private MypageRepository mypageRepository;
    private UserRepository userRepository;
    private MemberService memberService;

    @Autowired
    public MypageService(MypageRepository mypageRepository, UserRepository userRepository,MemberService memberService) {
        this.mypageRepository = mypageRepository;
        this.userRepository = userRepository;
        this.memberService = memberService;
    }

    public MypageDto findUser(Long userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        MypageDto mypageDto = new MypageDto();
        mypageDto.setUserId(user.getUserId());
        mypageDto.setNickname(user.getNickname());
        mypageDto.setUserImg(user.getUserImg());
        mypageDto.setStack(user.getStack());
        mypageDto.setJob(user.getJob());
        mypageDto.setPosition(user.getPosition());
        mypageDto.setTemperature(user.getTemperature());


        return mypageDto;
    }

    public MypageDto updateUser(Long userId, MypageDto mypageDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (mypageDto.getNickname() != null && !mypageDto.getNickname().equals(user.getNickname())) {
            boolean isExist = memberService.checkNicknameByUserNickname(mypageDto.getNickname());
            if (isExist) {
                throw new RuntimeException("이미 사용 중인 닉네임입니다.");
            }
            user.setNickname(mypageDto.getNickname());
        }

        if (mypageDto.getUserImg() != null) {
            user.setUserImg(mypageDto.getUserImg());
        }
        if (mypageDto.getStack() != null && !mypageDto.getStack().isEmpty()) {
            user.setStack(mypageDto.getStack());
        }
        if (mypageDto.getJob() != null) {
            user.setJob(mypageDto.getJob());
        }
        if (mypageDto.getPosition() != null) {
            user.setPosition(mypageDto.getPosition());
        }

        // 사용자 정보 저장
        userRepository.save(user);
        return mypageDto;
    }
}

