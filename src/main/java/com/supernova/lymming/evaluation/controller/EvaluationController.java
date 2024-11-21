package com.supernova.lymming.evaluation.controller;

import com.supernova.lymming.evaluation.dto.EvaluationDto;
import com.supernova.lymming.evaluation.entity.EvaluationEntity;
import com.supernova.lymming.evaluation.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/vote/best/member")
    public ResponseEntity<String> evaluate(@RequestBody EvaluationDto evaluationDto) {
        evaluationService.evalationBestMember(evaluationDto);
        return ResponseEntity.ok("베스트 팀원 평가가 완료되었습니다.");
    }
}
