package com.supernova.lymming.sharepage.controller;

import com.supernova.lymming.sharepage.dto.SharePageDto;
import com.supernova.lymming.sharepage.dto.ShareTeamAddDto;
import com.supernova.lymming.sharepage.dto.ShareUserInfoDto;
import com.supernova.lymming.sharepage.entity.End;
import com.supernova.lymming.sharepage.service.SharePageService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
    @ApiOperation(value = "공유페이지 보기", notes = "자신이 속한 공유페이지 상세 보기 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<SharePageDto>> getSharePage() {
        List<SharePageDto> sharePageDto = sharePageService.getSharePageList();
        return ResponseEntity.ok().body(sharePageDto);
    }

    //공유페이지 목록
    @GetMapping("/share/list")
    @ApiOperation(value = "공유페이지 목록", notes = "자신이 속한 공유페이지 목록 보여주는 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<SharePageDto>> mySharePage() {
        List<SharePageDto> sharePageDto = sharePageService.getSharePageList();
        return ResponseEntity.ok().body(sharePageDto);
    }

    //리더만 수정할 수 있도록 공유페이지 수정
    @PutMapping("/share/details/{sharePageId}/leader")
    @ApiOperation(value = "공유페이지 수정", notes = "리더만 수정할 수 있는 공유페이지 권한 체크 밋 수정 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<SharePageDto> leaderSharePage(@PathVariable Long sharePageId, @RequestBody SharePageDto sharePageDto) {
        // 권한 체크 및 게시물 업데이트
        SharePageDto updatedSharePage = sharePageService.checkSharePage(sharePageId,sharePageDto);// 권한 확인 후 업데이트
        return ResponseEntity.ok().body(updatedSharePage);  // SharePageDto를 리턴
    }

    @PutMapping("/share/details/{sharePageId}/end")
    @ApiOperation(value = "공유페이지 종류", notes = "리더가 공유페이지 종료 시 실행되는 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<SharePageDto> endSharePage(@PathVariable Long sharePageId) {
        // 종료 상태로 업데이트할 SharePageDto 생성
        SharePageDto sharePageDto = new SharePageDto();
        sharePageDto.setEnd(End.TRUE);  // 종료 상태로 설정

        // 권한 확인 및 업데이트
        SharePageDto updatedSharePage = sharePageService.end(sharePageId);

        return ResponseEntity.ok().body(updatedSharePage);
    }

    @GetMapping("/share/find/{nickname}/{sharePageId}")
    @ApiOperation(value = "팀원 네임 조회", notes = "공유페이지 팀원 초대 시 닉네임 조회로 사용되는 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<ShareUserInfoDto> findUser(@PathVariable String nickname, @PathVariable Long sharePageId) throws UnsupportedEncodingException {
        // URL 디코딩
        String decodedNickname = URLDecoder.decode(nickname, "UTF-8");

        // 서비스 레벨에서 해당 닉네임과 sharePageId를 처리
        ShareUserInfoDto shareUserInfoDto = sharePageService.getUserNickname(decodedNickname, sharePageId);

        // ResponseEntity로 ShareUserInfoDto 반환
        return ResponseEntity.ok().body(shareUserInfoDto);
    }

    @PostMapping("/share/add/team/member")
    @ApiOperation(value = "팀원 추가", notes = "팀원이 수락했을 때 사용되는 팀원추가 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<ShareTeamAddDto> addTeamMember(@RequestBody ShareTeamAddDto shareTeamAddDto) {
        try{
            ShareTeamAddDto updateDto = sharePageService.addTeamMember(shareTeamAddDto.getSharePageId(),shareTeamAddDto.getNickname());
            return ResponseEntity.ok().body(updateDto);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(null);
        }
    }
}
