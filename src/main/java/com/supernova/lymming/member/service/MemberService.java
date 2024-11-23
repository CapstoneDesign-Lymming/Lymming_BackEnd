package com.supernova.lymming.member.service;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.dto.SignupDto;
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
import java.util.concurrent.ThreadLocalRandom;
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
        return existNickname;
    }

    public List<MemberInfoDto> getRandomUsersByDeveloperType(Long userId){
        List<BoardEntity> boardEntities = boardRepository.findAll();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        int currentUserDeveloperType = currentUser.getDeveloper_type();

        List<User> allUser = userRepository.findAll().stream()
                .filter(user -> user.getDeveloper_type().equals(currentUserDeveloperType) && !user.getUserId().equals(userId))
                .collect(Collectors.toList());

        //동시성 문제를 해결하기 위해 각 쓰레드마다 생성된 인스턴스에서 각각 난수를 반환하는 ThreadLocalRandom 사용
        //random은 전역적으로 난수를 발생시키기 때문에 쓰레드가 한번에 몰리면 서버가 먹통이 될 수 있다.
        //Collection.shuffle()은 배열과 리스트를 랜덤으로 섞어준다.

        Collections.shuffle(allUser, ThreadLocalRandom.current());
        List<User> randomUsers = allUser.stream()
                .limit(3)
                .collect(Collectors.toList());

        List<MemberInfoDto> memberInfoDtos = randomUsers.stream()
                .map(user -> {
                    MemberInfoDto dto = new MemberInfoDto();
                    dto.setUserId(user.getUserId());
                    dto.setNickname(user.getNickname());
                    dto.setUserId(user.getUserId());
                    dto.setUserImg(user.getUserImg());
                    dto.setStack(Collections.singletonList(user.getStack()));
                    dto.setJob(user.getJob());
                    dto.setBio(user.getBio());
                    dto.setPosition(user.getPosition());
                    dto.setDevStyle(Collections.singletonList(user.getDevStyle()));
                    dto.setTemperature(user.getTemperature());

                    List<String> projectNames = boardEntities.stream()
                            .filter(board -> board.getUser().getUserId().equals(user.getUserId())) // 사용자 ID가 일치하는 게시판만 필터링
                            .map(BoardEntity::getProjectName)
                            .collect(Collectors.toList());

                    List<LocalDate> deadlines = boardEntities.stream()
                            .filter(board -> board.getUser().getUserId().equals(user.getUserId())) // 사용자 ID가 일치하는 게시판만 필터링
                            .map(BoardEntity::getDeadline)
                            .collect(Collectors.toList());

                    dto.setProjectNames(projectNames);
                    dto.setDeadlines(deadlines);

                    return dto;
                })
                .collect(Collectors.toList());
        return memberInfoDtos;
    }
}


