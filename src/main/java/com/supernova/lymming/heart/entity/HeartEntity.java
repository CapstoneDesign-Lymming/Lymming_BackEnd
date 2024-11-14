package com.supernova.lymming.heart.entity;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.github.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "heart")
public class HeartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_id")
    private Long heartId;

    @ManyToOne  // User 엔티티와의 관계 설정 , referenceedColumnName은 테이블의 참조할 컬럼명
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User userId;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private BoardEntity projectId;
}
