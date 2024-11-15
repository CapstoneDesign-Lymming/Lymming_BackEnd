package com.supernova.lymming.member.service;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.member.dto.MemberInfoDto;
import com.supernova.lymming.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public MemberService(MemberRepository memberRepository, UserRepository userRepository, BoardRepository boardRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    // 사용자 정보와 프로젝트 정보를 가져온다
    public MemberInfoDto findUserInfo(Long userId, Long projectId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));


        BoardEntity project = boardRepository.findByProjectId(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        //사용자 정보와 프로젝트 정보를 MemberInfoDto로 변환하여 return
        return convertUserAndProjectInfo(user, project);
    }

    // 사용자 정보와 프로젝트 정보를 변환하여 DTO로 바꾸기
    private MemberInfoDto convertUserAndProjectInfo(User user, BoardEntity project) {
        MemberInfoDto memberInfoDto = new MemberInfoDto();

        memberInfoDto.setUserId(user.getUserId());
        memberInfoDto.setNickname(user.getNickname());
        memberInfoDto.setUserImg(user.getUserImg());
        memberInfoDto.setStack(Collections.singletonList(user.getStack()));
        memberInfoDto.setJob(user.getJob());
        memberInfoDto.setPosition(user.getPosition());
        memberInfoDto.setDevStyle(Collections.singletonList(user.getDevStyle()));
        memberInfoDto.setTemperature(user.getTemperature());

        memberInfoDto.setProjectId(project.getProjectId());
        memberInfoDto.setProjectName(project.getProjectName());
        memberInfoDto.setDeadline(project.getDeadline());

        return memberInfoDto;
    }

}
