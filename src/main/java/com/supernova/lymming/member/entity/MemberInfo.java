package com.supernova.lymming.member.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.github.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "member_info")
public class MemberInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne  // User 엔티티와의 관계 설정 , referenceedColumnName은 테이블의 참조할 컬럼명
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private BoardEntity board;

    @Column(name = "position")
    private String position;

    @Column(name = "dev_style")
    private String devStyle;

    @Column(name = "user_img")
    private String userImg;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "stack")
    private String stack;

    @Column(name = "job")
    private String job;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "deadline",nullable = false)
    @JsonFormat(pattern = "yyyy MM dd")
    private LocalDate deadline;

}
