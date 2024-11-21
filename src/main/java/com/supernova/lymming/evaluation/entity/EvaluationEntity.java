package com.supernova.lymming.evaluation.entity;


import com.supernova.lymming.github.entity.User;
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
}
