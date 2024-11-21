package com.supernova.lymming.evaluation.dto;

import com.supernova.lymming.evaluation.entity.VoteStatus;
import lombok.Data;

@Data
public class EvaluationDto {
    private Long evaluationId;
    private String nickname;
    private String bestMember;
    private VoteStatus voteStatus;
    private Long sharePageId;
}
