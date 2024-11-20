package com.supernova.lymming.sharepage.controller;

import com.supernova.lymming.sharepage.dto.SharePageDto;
import com.supernova.lymming.sharepage.entity.End;
import com.supernova.lymming.sharepage.service.SharePageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
public class SharePageController {

    private final SharePageService sharePageService;

    public SharePageController(SharePageService sharePageService) {
        this.sharePageService = sharePageService;
    }

    //공유페이지 보기
    @GetMapping("/share/detail/{share_page_id}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<SharePageDto>> getSharePage() {
        List<SharePageDto> sharePageDto = sharePageService.getSharePageList();
        return ResponseEntity.ok().body(sharePageDto);
    }

    //공유페이지 목록
    @GetMapping("/share/list")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<SharePageDto>> mySharePage() {
        List<SharePageDto> sharePageDto = sharePageService.getSharePageList();
        log.info("리턴된 sharPage dto는 :{} ", sharePageDto);
        return ResponseEntity.ok().body(sharePageDto);
    }

    //리더만 수정할 수 있도록 공유페이지 수정
    @PutMapping("/share/details/{sharePageId}/leader")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<SharePageDto> leaderSharePage(@PathVariable Long sharePageId, @RequestBody SharePageDto sharePageDto) {
        // 권한 체크 및 게시물 업데이트
        log.info("sharePAgeId:{}", sharePageId);
        SharePageDto updatedSharePage = sharePageService.checkSharePage(sharePageId,sharePageDto);// 권한 확인 후 업데이트
        log.info("updateSharePage:{}", updatedSharePage);
        return ResponseEntity.ok().body(updatedSharePage);  // SharePageDto를 리턴
    }

    @PutMapping("share/details/{sharePageId}/end")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<SharePageDto> endSharePage(@PathVariable Long sharePageId) {
        // 종료 상태로 업데이트할 SharePageDto 생성
        SharePageDto sharePageDto = new SharePageDto();
        sharePageDto.setEnd(End.TRUE);  // 종료 상태로 설정

        log.info("종료된 sharePageId: {}", sharePageId);

        // 권한 확인 및 업데이트
        SharePageDto updatedSharePage = sharePageService.end(sharePageId);
        log.info("updatedSharePage: {}", updatedSharePage);

        return ResponseEntity.ok().body(updatedSharePage);
    }

}
