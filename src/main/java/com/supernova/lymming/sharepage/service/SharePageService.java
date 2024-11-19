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

    public SharePageDto checkSharePage(SharePageDto sharePageDto) {
        log.info("권한체크 시작");

        Long currentUserId = sharePageDto.getUserId();  // 현재 사용자 ID
        log.info("currentUserId : {}", currentUserId);

        // 사용자 ID에 맞는 SharePage 조회
        SharePageEntity sharePage = sharePageRepository.findByUser_UserId(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자에 대한 SharePage가 존재하지 않습니다."));

        // 해당 SharePage ID를 가져옴
        Long shareId = sharePage.getSharePageId();
        log.info("userId에 해당하는 SharePage ID: {}", shareId);

        // 프로젝트의 소유자 확인
        Long projectOwner = sharePage.getBoard().getUser().getUserId();
        log.info("게시글 작성자 ID: {}", projectOwner);

        // 권한 확인
        if (currentUserId.equals(projectOwner)) {
            log.info("권한 확인 완료. 수정 가능합니다.");

            // 권한이 있으면 업데이트 메서드 호출
            return update(shareId, sharePageDto);
        } else {
            log.warn("권한 없음. 요청 거부됨.");
            throw new SecurityException("수정 권한이 없습니다.");
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
                    sharePage.getBoard().getProjectImg(),
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
        } else {
            sharePage.setEnd(sharePageDto.getEnd());  // 사용자가 종료 버튼을 눌렀을 때 TRUE로 설정
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
                sharePage.getBoard().getProjectImg(),
                sharePage.getTeamName(),
                sharePage.getEnd(), // 변경된 'end' 값 리턴
                sharePage.getLeader()
        );
    }



}
