package com.supernova.lymming.evaluation.controller;

import com.supernova.lymming.evaluation.dto.EvaluationDto;
import com.supernova.lymming.evaluation.entity.EvaluationEntity;
import com.supernova.lymming.evaluation.entity.VoteStatus;
import com.supernova.lymming.evaluation.service.EvaluationService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@RestController
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/vote/best/member")
    @ApiOperation(value = "팀원 투표하기", notes = "공유페이지 종료 후 팀원 평가 API, Token 필요")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<String> evaluate(@RequestBody EvaluationDto evaluationDto) {
        evaluationService.evalationBestMember(evaluationDto);
        return ResponseEntity.ok("베스트 팀원 평가가 완료되었습니다.");
    }

    @GetMapping("/vote/has/user")
    @ApiOperation(value = "투표 여부 확인", notes = "투표 여부를 확인하는 API, Token 필요")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<VoteStatus> getUserVoteStatus(
            @RequestParam Long sharePageId,
            @RequestParam String nickname) throws UnsupportedEncodingException {
        String decodedNickname = URLDecoder.decode(nickname, "UTF-8");
        VoteStatus voteStatus = evaluationService.getUserVoteStatus(sharePageId, decodedNickname);
        return ResponseEntity.ok(voteStatus);
    }
}
