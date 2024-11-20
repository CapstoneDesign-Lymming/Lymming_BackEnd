package com.supernova.lymming.sharepage.service;

import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.sharepage.dto.SharePageDto;
import com.supernova.lymming.sharepage.entity.End;
import com.supernova.lymming.sharepage.entity.SharePageEntity;
import com.supernova.lymming.sharepage.repository.SharePageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class SharePageService {

    private final SharePageRepository sharePageRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public SharePageService(SharePageRepository sharePageRepository, BoardRepository boardRepository) {
        this.sharePageRepository = sharePageRepository;
        this.boardRepository = boardRepository;
    }

    public SharePageDto checkSharePage(Long sharePageId ,SharePageDto sharePageDto) {
        log.info("sharePageId : {}", sharePageId);

        Long currentUserId = sharePageDto.getUserId();
        log.info("currentUserId : {}", currentUserId);

        SharePageEntity sharePage = sharePageRepository.findBySharePageId(sharePageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 SharePage가 존재하지 않습니다."));


        Long projectOwner = sharePage.getBoard().getUser().getUserId();

        if (currentUserId.equals(projectOwner)) {
            log.info("권한이 확인. 수정 가능합니다.");
            return update(sharePageId, sharePageDto);
        } else {
            log.info("권한이 없습니다");
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
}
