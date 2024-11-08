package com.supernova.lymming.sharepage.controller;


import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.sharepage.Dto.SharePageDto;
import com.supernova.lymming.sharepage.Repository.SharePageRepository;
import com.supernova.lymming.sharepage.Service.SharePageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SharePageController {

    private SharePageService sharePageService;

    public SharePageController(SharePageService sharePageService) {
        this.sharePageService = sharePageService;
    }

    @GetMapping("/share/detail/{share_page_id}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<SharePageDto>> getSharePage() {
        List<SharePageDto> sharePageDto = sharePageService.getSharePageList();
        return ResponseEntity.ok().body(sharePageDto);
    }


    @GetMapping("/share/list")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<SharePageDto>> mySharePage() {
        List<SharePageDto> sharePageDto = sharePageService.getSharePageList();
        return ResponseEntity.ok().body(sharePageDto);
    }

    @PutMapping("/share/details/{sharePageId}/leader")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<SharePageDto> leaderSharePage(@PathVariable Long sharePageId, @RequestBody SharePageDto sharePageDto) {
        // 권한 체크 및 게시물 업데이트
        SharePageDto updatedSharePage = sharePageService.checkSharePage(sharePageDto);  // 권한 확인 후 업데이트
        return ResponseEntity.ok().body(updatedSharePage);  // SharePageDto를 리턴
    }
}
