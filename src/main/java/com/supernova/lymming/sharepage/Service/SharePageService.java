package com.supernova.lymming.sharepage.Service;

import com.supernova.lymming.sharepage.Dto.SharePageDto;
import com.supernova.lymming.sharepage.Entity.SharePageEntity;
import com.supernova.lymming.sharepage.Repository.SharePageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SharePageService {

    private final SharePageRepository sharePageRepository;

    @Autowired
    public SharePageService(SharePageRepository sharePageRepository) {
        this.sharePageRepository = sharePageRepository;
    }

    public SharePageDto createSharePage(SharePageDto sharePageDto) {
        SharePageEntity sharePage = new SharePageEntity();
        log.info("들어옴");

        // userId를 설정
        sharePage.setUserId(sharePageDto.getUserId());
        log.info("userId");

        sharePage.setProjectId(sharePageDto.getProjectId());
        log.info("projectId");

        sharePage.setTeamName(sharePageDto.getTeamName());
        log.info("teamName");

        sharePage.setContent(sharePageDto.getContent());
        log.info("content");

        sharePage.setImageUrl1(sharePageDto.getImageUrl1());
        sharePage.setImageUrl2(sharePageDto.getImageUrl2());
        sharePage.setImageUrl3(sharePageDto.getImageUrl3());
        sharePage.setImageUrl4(sharePageDto.getImageUrl4());
        sharePage.setTeamMember(sharePageDto.getTeamMember());
        sharePage.setProjectLink(sharePageDto.getProjectLink());

        SharePageEntity savedPage = sharePageRepository.save(sharePage);

        // 저장된 엔티티 ID를 DTO에 설정해 반환
        sharePageDto.setSharePageId(savedPage.getSharePageId());
        return sharePageDto;
    }
}


