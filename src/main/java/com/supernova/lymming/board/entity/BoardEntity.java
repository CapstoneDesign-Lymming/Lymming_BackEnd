package com.supernova.lymming.board.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="project")
@Data
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    //유저 아이디는 user 테이블의 유저아이디
    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String recruitmentCount;

    //진행방식
    @Column(nullable = false)
    private String projectMethod;

    //프로젝트 기간
    @Column(nullable = false)
    private String projectDuration;

    //모집 마감일
    @Column(nullable = false)
    private String recruitmentDeadline;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String developmentStyle;

}