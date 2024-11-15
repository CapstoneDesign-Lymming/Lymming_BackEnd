package com.supernova.lymming.sharepage.controller;

import com.supernova.lymming.sharepage.dto.SharePageDto;
import com.supernova.lymming.sharepage.service.SharePageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
        return ResponseEntity.ok().body(sharePageDto);
    }

    //리더만 수정할 수 있도록 공유페이지 수정
    @PutMapping("/share/details/{sharePageId}/leader")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<SharePageDto> leaderSharePage(@PathVariable Long sharePageId, @RequestBody SharePageDto sharePageDto) {
        // 권한 체크 및 게시물 업데이트
        SharePageDto updatedSharePage = sharePageService.checkSharePage(sharePageDto);  // 권한 확인 후 업데이트
        return ResponseEntity.ok().body(updatedSharePage);  // SharePageDto를 리턴
    }
}
