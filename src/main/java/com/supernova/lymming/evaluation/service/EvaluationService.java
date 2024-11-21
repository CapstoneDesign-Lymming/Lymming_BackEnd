package com.supernova.lymming.evaluation.service;

import com.supernova.lymming.evaluation.dto.EvaluationDto;
import com.supernova.lymming.evaluation.entity.EvaluationEntity;
import com.supernova.lymming.evaluation.entity.VoteStatus;
import com.supernova.lymming.evaluation.repository.EvaluationRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.sharepage.entity.End;
import com.supernova.lymming.sharepage.entity.SharePageEntity;
import com.supernova.lymming.sharepage.repository.SharePageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final UserRepository userRepository;
    private final SharePageRepository sharePageRepository;

    @Transactional
    public void evalationBestMember(EvaluationDto evaluationDto){

        // 접근한 공유페이지 id
        Long currentShareId = evaluationDto.getSharePageId();
        log.info("접근한 공유페이지 id : {}", currentShareId);

        SharePageEntity sharePage = sharePageRepository.findBySharePageId(currentShareId)
                .orElseThrow(() -> new IllegalArgumentException("해당 SharePage가 존재하지 않습니다."));

        log.info("접근한 공유페이지 id 2번째 : {}", sharePage);

        //접근한 사람의 닉네임
        String currentUserNickname = evaluationDto.getNickname();

        log.info("접근한 사용자 닉네임 : {}", currentUserNickname);

        if(!sharePage.getTeamMember().contains(currentUserNickname)){
            throw new IllegalArgumentException("팀 멤버가 아닙니다");
        }

        log.info("if문 무사히 통과");

        Optional<EvaluationEntity> existingEvalation = evaluationRepository.findByNicknameAndSharePageId(currentUserNickname,currentShareId);
        log.info("이미 투표한 사용자인지 찾기 : {}",existingEvalation);

        if(existingEvalation.isPresent()) {
            throw new IllegalArgumentException("이미 투표한 사용자입니다");
        }else{
            User bestMember = userRepository.findByNickname(evaluationDto.getBestMember())
                    .orElseThrow(() -> new IllegalArgumentException("베스트 멤버를 찾을 수 없습니다."));

            bestMember.setTemperature(bestMember.getTemperature()+1);
            userRepository.save(bestMember);

            // EvaluationEntity 객체 생성 및 설정
            EvaluationEntity evaluationEntity = new EvaluationEntity();
            evaluationEntity.setEvaluationId(evaluationDto.getEvaluationId());
            evaluationEntity.setNickname(evaluationDto.getNickname());
            evaluationEntity.setBestMember(evaluationDto.getBestMember());
            evaluationEntity.setVoteStatus(VoteStatus.TRUE);
            evaluationEntity.setSharePageId(currentShareId);

            // EvaluationEntity 저장
            evaluationRepository.save(evaluationEntity);
        }

    }
}
