package com.supernova.lymming.member.service;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.member.dto.MemberInfoDto;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service

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
            memberInfoDto.setDevStyle(Collections.singletonList(user.getDevStyle()));  // 동일하게 리스트 처리
            if (user.getTemperature() == null) {
                user.setTemperature(0.0f); // 기본값 설정
            }

            // 해당 사용자가 작성한 게시판 정보 추가
            for (BoardEntity boardEntity : boardEntities) {
                if (boardEntity.getUser().getUserId().equals(user.getUserId())) {
                    // 게시판이 해당 사용자의 게시물이라면
                    memberInfoDto.setProjectName(boardEntity.getProjectName());
                    memberInfoDto.setDeadline(boardEntity.getDeadline());


                }
            }

            // MypageDto 생성 후 user 정보와 게시판 정보 포함
            MemberInfoDto memberDto = new MemberInfoDto();
            memberDto.setMemberInfo(memberInfoDto);

            // MypageDto 리스트에 추가
            memberInfoDtos.add(memberDto);
        }

        return memberInfoDtos;
    }

    public MemberInfoDto getUserInfoByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        List<BoardEntity> boardEntities = boardRepository.findByUser_UserId(userId);

        MemberInfoDto memberInfoDto = new MemberInfoDto();
        memberInfoDto.setNickname(user.getNickname());
        memberInfoDto.setUserImg(user.getUserImg());
        memberInfoDto.setStack(Collections.singletonList(user.getStack()));
        memberInfoDto.setJob(user.getJob());
        memberInfoDto.setPosition(user.getPosition());
        memberInfoDto.setBio(user.getBio());
        memberInfoDto.setDevStyle(Collections.singletonList(user.getDevStyle()));
        if (user.getTemperature() == null) {
            user.setTemperature(36.5f); // 기본값 설정
        }

        // 해당 사용자가 작성한 게시판 정보 추가
        if (!boardEntities.isEmpty()) {
            BoardEntity boardEntity = boardEntities.get(0);  // 게시판 하나만 예시로 가져옴
            memberInfoDto.setProjectId(boardEntity.getProjectId());
            memberInfoDto.setProjectName(boardEntity.getProjectName());
            memberInfoDto.setDeadline(boardEntity.getDeadline());
        }

        return memberInfoDto;
    }

    public boolean checkNicknameByUserNickname(String nickname){

    }
}


