package com.supernova.lymming.mypage.service;

import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.mypage.dto.MypageDto;
import com.supernova.lymming.mypage.repository.MypageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
public class MypageService {

    private MypageRepository mypageRepository;
    private UserRepository userRepository;

    @Autowired
    public MypageService(MypageRepository mypageRepository, UserRepository userRepository) {
        this.mypageRepository = mypageRepository;
        this.userRepository = userRepository;
    }

    public MypageDto findUser(Long userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        MypageDto mypageDto = new MypageDto();
        mypageDto.setUserId(user.getUserId());
        mypageDto.setNickname(user.getNickname());
        mypageDto.setUserImg(user.getUserImg());
        mypageDto.setStack(Collections.singletonList(user.getStack()));
        mypageDto.setJob(user.getJob());
        mypageDto.setPosition(user.getPosition());
        mypageDto.setDevStyle(Collections.singletonList(user.getDevStyle()));
        if (user.getTemperature() == null) {
            user.setTemperature(0.0f); // 기본값 설정
        }

        return mypageDto;
    }

    public MypageDto updateUser(Long userId, MypageDto mypageDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        if (mypageDto.getNickname() != null) {
            user.setNickname(mypageDto.getNickname());
        }
        if (mypageDto.getUserImg() != null) {
            user.setUserImg(mypageDto.getUserImg());
        }
        if (mypageDto.getStack() != null && !mypageDto.getStack().isEmpty()) {
            user.setStack(mypageDto.getStack().get(0)); // stack은 단일 항목 리스트
        }
        if (mypageDto.getJob() != null) {
            user.setJob(mypageDto.getJob());
        }
        if (mypageDto.getPosition() != null) {
            user.setPosition(mypageDto.getPosition());
        }
        if (mypageDto.getDevStyle() != null && !mypageDto.getDevStyle().isEmpty()) {
            user.setDevStyle(mypageDto.getDevStyle().get(0)); // devStyle은 리스트 형태
        }

        // 사용자 정보 저장
        userRepository.save(user);
        return mypageDto;
    }
}

