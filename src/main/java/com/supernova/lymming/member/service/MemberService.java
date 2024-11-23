package com.supernova.lymming.member.service;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.member.dto.MemberInfoDetailDto;
import com.supernova.lymming.member.dto.MemberInfoDto;
import com.supernova.lymming.member.repository.MemberRepository;
import com.supernova.lymming.member.repository.MemberRepository2;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MemberService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final MemberRepository2 memberRepository2;

    public MemberService(BoardRepository boardRepository, UserRepository userRepository, MemberRepository memberRepository, MemberRepository2 memberRepository2) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.memberRepository2 = memberRepository2;
    }

    public List<MemberInfoDto> getUserList() {
        List<User> users = userRepository.findAll();  // 모든 사용자 가져오기
        List<BoardEntity> boardEntities = boardRepository.findAll();  // 모든 게시판 엔티티 가져오기
        List<MemberInfoDto> memberInfoDtos = new ArrayList<>();

        // 사용자 정보를 매핑
        for (User user : users) {
            MemberInfoDto memberInfoDto = new MemberInfoDto();
            memberInfoDto.setUserId(user.getUserId());
            memberInfoDto.setNickname(user.getNickname());
            memberInfoDto.setUserImg(user.getUserImg());
            memberInfoDto.setStack(Collections.singletonList(user.getStack()));
            memberInfoDto.setJob(user.getJob());
            memberInfoDto.setBio(user.getBio());
            memberInfoDto.setPosition(user.getPosition());
            memberInfoDto.setDevStyle(Collections.singletonList(user.getDevStyle()));
            memberInfoDto.setTemperature(user.getTemperature());

            // 사용자와 관련된 게시판 정보 가져오기
            List<String> projectNames = boardEntities.stream()
                    .filter(board -> board.getUser().getUserId().equals(user.getUserId())) // 사용자 ID가 일치하는 게시판만 필터링
                    .map(BoardEntity::getProjectName)
                    .collect(Collectors.toList());

            List<LocalDate> deadlines = boardEntities.stream()
                    .filter(board -> board.getUser().getUserId().equals(user.getUserId())) // 사용자 ID가 일치하는 게시판만 필터링
                    .map(BoardEntity::getDeadline)
                    .collect(Collectors.toList());

            memberInfoDto.setProjectNames(projectNames);
            memberInfoDto.setDeadlines(deadlines);

            memberInfoDtos.add(memberInfoDto); // 리스트에 추가
        }

        return memberInfoDtos;
    }

    public MemberInfoDetailDto getUserInfoByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        MemberInfoDetailDto memberInfoDetailDto = new MemberInfoDetailDto();
        memberInfoDetailDto.setUserId(userId);
        memberInfoDetailDto.setNickname(user.getNickname());
        memberInfoDetailDto.setUserImg(user.getUserImg());
        memberInfoDetailDto.setDevStyle(Collections.singletonList(user.getDevStyle()));
        memberInfoDetailDto.setTemperature(user.getTemperature());
        memberInfoDetailDto.setBio(user.getBio());
        memberInfoDetailDto.setJob(user.getJob());
        memberInfoDetailDto.setPosition(user.getPosition());

        return memberInfoDetailDto;
    }

    public boolean checkNicknameByUserNickname(String nickname) {
        boolean existNickname = memberRepository2.existsByNickname(nickname);
        return !existNickname;
    }
}


