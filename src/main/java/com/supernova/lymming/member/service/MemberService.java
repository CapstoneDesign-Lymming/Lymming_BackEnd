package com.supernova.lymming.member.service;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.member.dto.MemberInfoDetailDto;
import com.supernova.lymming.member.dto.MemberInfoDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
public class MemberService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public MemberService(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public List<MemberInfoDto> getUserList() {
        List<User> users = userRepository.findAll();  // 모든 사용자 가져오기
        List<BoardEntity> boardEntities = boardRepository.findAll();  // 모든 게시판 엔티티 가져오기
        List<MemberInfoDto> memberInfoDtos = new ArrayList<>();

        // 사용자 정보를 MypageDto로 매핑
        for (User user : users) {
            MemberInfoDto memberInfoDto = new MemberInfoDto();

            memberInfoDto.setUserId(user.getUserId());
            memberInfoDto.setNickname(user.getNickname());
            memberInfoDto.setUserImg(user.getUserImg());
            memberInfoDto.setStack(Collections.singletonList(user.getStack()));  // 단일 값이면 리스트로 감싸는 방식
            memberInfoDto.setJob(user.getJob());
            memberInfoDto.setPosition(user.getPosition());
            memberInfoDto.setBio(user.getBio());
            memberInfoDto.setDevStyle(Collections.singletonList(user.getDevStyle()));

            // MypageDto 생성 후 user 정보와 게시판 정보 포함
            MemberInfoDto memberDto = new MemberInfoDto();
            memberDto.setMemberInfo(memberInfoDto);

            // MypageDto 리스트에 추가
            memberInfoDtos.add(memberDto);
        }

        return memberInfoDtos;
    }

    public MemberInfoDetailDto getUserInfoByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        List<BoardEntity> boardEntities = boardRepository.findByUser_UserId(userId);

        MemberInfoDetailDto memberInfoDetailDto = new MemberInfoDetailDto();
        memberInfoDetailDto.setNickname(user.getNickname());
        memberInfoDetailDto.setUserImg(user.getUserImg());
        memberInfoDetailDto.setDevStyle(Collections.singletonList(user.getDevStyle()));
        memberInfoDetailDto.setTemperature(user.getTemperature());

        List<String> projectNames = new ArrayList<>();
        List<LocalDate> deadlines = new ArrayList<>();

        if (!boardEntities.isEmpty()) {
            for (BoardEntity boardEntity : boardEntities) {
                // 각 게시글의 projectName과 deadline을 리스트에 추가
                projectNames.add(boardEntity.getProjectName());
                deadlines.add(boardEntity.getDeadline());
            }
        }

        // MemberInfoDto에 모든 게시글의 projectName과 deadline 저장
        memberInfoDetailDto.setProjectNames(projectNames);
        memberInfoDetailDto.setDeadlines(deadlines);

        return memberInfoDetailDto;
    }

    public boolean checkNicknameByUserNickname(String nickname){

    }
}


