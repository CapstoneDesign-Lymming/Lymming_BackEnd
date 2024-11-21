package com.supernova.lymming.evaluation.service;

import com.supernova.lymming.evaluation.dto.EvaluationDto;
import com.supernova.lymming.evaluation.entity.EvaluationEntity;
import com.supernova.lymming.evaluation.repository.EvaluationRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void evalationBestMember(EvaluationDto evaluationDto){
        // userRepository에서 bestMember 찾기
        User bestMember = userRepository.findByNickname(evaluationDto.getBestMember())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // temperature 값을 수정
        bestMember.setTemperature(bestMember.getTemperature() + 1);

        // UserEntity를 저장 (이때 변경된 temperature도 저장됨)
        userRepository.save(bestMember);

        // EvaluationEntity 객체 생성 및 설정
        EvaluationEntity evaluationEntity = new EvaluationEntity();
        evaluationEntity.setEvaluationId(evaluationDto.getEvaluationId());
        evaluationEntity.setNickname(evaluationDto.getNickname());
        evaluationEntity.setBestMember(evaluationDto.getBestMember());

        // EvaluationEntity 저장
        evaluationRepository.save(evaluationEntity);
    }
}
