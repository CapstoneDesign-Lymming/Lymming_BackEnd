package com.supernova.lymming.sharepage.service;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.sharepage.dto.SharePageDto;
import com.supernova.lymming.sharepage.dto.ShareTeamAddDto;
import com.supernova.lymming.sharepage.dto.ShareUserInfoDto;
import com.supernova.lymming.sharepage.entity.End;
import com.supernova.lymming.sharepage.entity.SharePageEntity;
import com.supernova.lymming.sharepage.repository.SharePageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class SharePageService {

    private final SharePageRepository sharePageRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Autowired
    public SharePageService(SharePageRepository sharePageRepository, BoardRepository boardRepository, UserRepository userRepository) {
        this.sharePageRepository = sharePageRepository;
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    }

    public SharePageDto checkSharePage(Long sharePageId ,SharePageDto sharePageDto) {

        Long currentUserId = sharePageDto.getUserId();
        SharePageEntity sharePage = sharePageRepository.findBySharePageId(sharePageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 SharePage가 존재하지 않습니다."));


        Long projectOwner = sharePage.getBoard().getUser().getUserId();

        if (currentUserId.equals(projectOwner)) {
            return update(sharePageId, sharePageDto);
        } else {
            throw new SecurityException("수정 권한이 없습니다");
        }
    }

    public List<SharePageDto> getSharePageList() {
        List<SharePageEntity> shasrePageList = sharePageRepository.findAll();
        List<SharePageDto> SharePageDtoList = new ArrayList<>();

        for (SharePageEntity sharePage : shasrePageList) {
            SharePageDto sharePageDto = new SharePageDto(
                    sharePage.getSharePageId(),
                    sharePage.getUser().getUserId(),
                    sharePage.getBoard().getProjectId(),
                    sharePage.getSharePageDescription(),
                    sharePage.getTeamMember(),
                    sharePage.getSharePageName(),
                    sharePage.getProjectLink(),
                    sharePage.getSharePageUrl(),
                    sharePage.getTeamName(),
                    sharePage.getEnd(),
                    sharePage.getLeader(),
                    sharePage.getMemberUrlBundle(),
                    sharePage.getPositionBundle()
            );
            SharePageDtoList.add(sharePageDto);
        }
        log.info("공유페이지 리스트 조회 : {}", SharePageDtoList);
        return SharePageDtoList;
    }


    public SharePageDto update(Long shareId, SharePageDto sharePageDto) {
        // 게시물 찾기
        SharePageEntity sharePage = sharePageRepository.findBySharePageId(shareId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));

        // 기본적으로 끝났는지 여부는 FALSE로 설정
        if (sharePageDto.getEnd() == null) {
            sharePage.setEnd(End.FALSE);  // 기본값 설정
        }

        // 게시글 업데이트
        sharePage.setSharePageName(sharePageDto.getSharePageName());
        sharePage.setSharePageDescription(sharePageDto.getSharePageDescription());
        sharePage.setProjectLink(sharePageDto.getProjectLink());
        sharePage.setTeamMember(sharePageDto.getTeamMember());
        sharePage.setSharePageUrl(sharePageDto.getSharePageUrl());
        sharePage.setTeamName(sharePageDto.getTeamName());

        // SharePageEntity를 저장
        sharePageRepository.save(sharePage);

        log.info("공유페이지 저장:{}", sharePage);

        // SharePageDto 리턴
        return new SharePageDto(
                sharePage.getSharePageId(),
                sharePage.getUser().getUserId(),
                sharePage.getBoard().getProjectId(),
                sharePage.getSharePageDescription(),
                sharePage.getTeamMember(),
                sharePage.getSharePageName(),
                sharePage.getProjectLink(),
                sharePage.getSharePageUrl(),
                sharePage.getTeamName(),
                sharePage.getEnd(), // 변경된 'end' 값 리턴
                sharePage.getLeader(),
                sharePage.getMemberUrlBundle(),
                sharePage.getPositionBundle()
        );
    }

    public SharePageDto end(Long sharePageId) {
        // 게시물 찾기
        SharePageEntity sharePage = sharePageRepository.findBySharePageId(sharePageId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));

        // 종료 상태를 TRUE로 설정
        sharePage.setEnd(End.TRUE);

        // 업데이트된 엔티티 저장
        sharePageRepository.save(sharePage);

        // DTO 반환
        return new SharePageDto(
                sharePage.getSharePageId(),
                sharePage.getUser().getUserId(),
                sharePage.getBoard().getProjectId(),
                sharePage.getSharePageDescription(),
                sharePage.getTeamMember(),
                sharePage.getSharePageName(),
                sharePage.getProjectLink(),
                sharePage.getSharePageUrl(),
                sharePage.getTeamName(),
                sharePage.getEnd(),
                sharePage.getLeader(),
                sharePage.getMemberUrlBundle(),
                sharePage.getPositionBundle()
        );
    }

    public ShareUserInfoDto getUserNickname(String nickname, Long sharePageId) {

        // UserRepository에서 닉네임 검색
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("닉네임을 찾을 수 없습니다: " + nickname));


        // 검색된 닉네임과 sharePageId를 ShareUserInfoDto로 반환
        return new ShareUserInfoDto(sharePageId, user.getNickname());
    }


    public ShareTeamAddDto addTeamMember(Long sharePageId, String nickname) {

        // 공유 페이지 조회
        SharePageEntity sharePage = sharePageRepository.findBySharePageId(sharePageId)
                .orElseThrow(() -> new RuntimeException("공유페이지를 찾을 수 없습니다"));

        // 사용자 조회
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        // 팀 멤버 업데이트
        List<String> teamMembers = new ArrayList<>(Arrays.asList(sharePage.getTeamMember().split(",")));
        teamMembers.add(nickname);
        sharePage.setTeamMember(String.join(",", teamMembers));

        log.info("팀 멤버 업데이트 : {}",sharePage.getTeamMember());

        // 기존 데이터를 리스트로 가져옴
        List<String> memberUrls = new ArrayList<>();
        if (sharePage.getMemberUrlBundle() != null) {
            memberUrls = new ArrayList<>(Arrays.asList(sharePage.getMemberUrlBundle().split(",")));
        }

        log.info("기존 데이터를 가져옴 : {}",memberUrls);

        List<String> positions = new ArrayList<>();
        if (sharePage.getPositionBundle() != null) {
            positions = new ArrayList<>(Arrays.asList(sharePage.getPositionBundle().split(",")));
        }

        log.info("기존 포지션을 가져옴 : {}",positions);

        // 새로운 사용자 추가
        memberUrls.add(user.getUserImg());
        positions.add(user.getPosition());

        log.info("추가된 사진: {}", memberUrls);
        log.info("추가된 포지션: {}", positions);

        // 프로젝트 모집 인원 확인
        Long projectId = sharePage.getBoard().getProjectId();
        BoardEntity board = boardRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("BoardEntity를 찾을 수 없습니다"));
        int maxSize = board.getRecruitmentCount();


        // 최대 크기를 초과한 경우, 에러
        if (memberUrls.size() > maxSize) {
            throw new RuntimeException("최대 모집 인원 수를 초과하여 추가할 수 없습니다.");
        }

        // 변경된 데이터 저장
        sharePage.setMemberUrlBundle(String.join(",", memberUrls));
        sharePage.setPositionBundle(String.join(",", positions));
        sharePageRepository.save(sharePage);

        log.info("sharePage : {}",sharePage);

        // 결과 반환
        return new ShareTeamAddDto(
                sharePageId,
                nickname,
                String.join(",", teamMembers),
                String.join(",", memberUrls),
                String.join(",", positions)
        );
    }



}
