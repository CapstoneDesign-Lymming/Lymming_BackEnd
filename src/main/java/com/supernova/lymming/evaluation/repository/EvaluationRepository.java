package com.supernova.lymming.evaluation.repository;

import com.supernova.lymming.evaluation.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository <EvaluationEntity,Long> {
    Optional<EvaluationEntity> findByNicknameAndSharePageId(String nickname, Long evaluationId);
}
