package com.supernova.lymming.sharepage.service;

import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.sharepage.dto.SharePageDto;
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
                    sharePage.getLeader()
            );
            SharePageDtoList.add(sharePageDto);
        }

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
                sharePage.getLeader()
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
                sharePage.getLeader()
        );
    }

    public String getUserNickname(String nickname) {
        log.info("getUserNickname에서 닉네임은 : {}", nickname);

        // UserRepository에서 닉네임 검색
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NoSuchElementException("닉네임을 찾을 수 없습니다: " + nickname));

        // 검색된 닉네임 반환
        return user.getNickname();
    }
}
