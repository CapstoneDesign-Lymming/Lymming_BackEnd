package com.supernova.lymming.evaluation.repository;

import com.supernova.lymming.evaluation.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends JpaRepository <EvaluationEntity,Long> {

}
