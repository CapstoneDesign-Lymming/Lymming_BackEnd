package com.supernova.lymming.evaluation.controller;

import com.supernova.lymming.evaluation.dto.EvaluationDto;
import com.supernova.lymming.evaluation.entity.EvaluationEntity;
import com.supernova.lymming.evaluation.entity.VoteStatus;
import com.supernova.lymming.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/vote/best/member")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<String> evaluate(@RequestBody EvaluationDto evaluationDto) {
        evaluationService.evalationBestMember(evaluationDto);
        return ResponseEntity.ok("베스트 팀원 평가가 완료되었습니다.");
    }

    @GetMapping("/vote/has/user")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<VoteStatus> getUserVoteStatus(
            @RequestParam Long sharePageId,
            @RequestParam String nickname) {
        VoteStatus voteStatus = evaluationService.getUserVoteStatus(sharePageId, nickname);
        return ResponseEntity.ok(voteStatus);
    }
}
