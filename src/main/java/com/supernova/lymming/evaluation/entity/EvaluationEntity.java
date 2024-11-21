package com.supernova.lymming.evaluation.entity;


import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.sharepage.entity.SharePageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="evaluation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationId;

    @Column(name = "best_member", nullable = false)
    private String bestMember;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "vote_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private VoteStatus voteStatus;

    @Column(name = "share_page_id")
    private Long sharePageId;
}
