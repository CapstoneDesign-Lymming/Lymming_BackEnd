package com.supernova.lymming.board.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.supernova.lymming.github.entity.User;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="project")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    //유저 아이디는 user 테이블의 유저아이디
    @ManyToOne  // User 엔티티와의 관계 설정 , referenceedColumnName은 테이블의 참조할 컬럼명
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name ="study_type",nullable = false)
    private String studyType;

    @Column(name = "upload_time",nullable = false)
    @JsonFormat(pattern = "yyyy MM dd")
    private LocalDate uploadTime;

    //모집분야
    @Column(name = "recruitment_field",nullable = false)
    private String recruitmentField;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "work_type",nullable = false)
    private String workType;

    @Column(name = "tech_stack",nullable = false)
    private String techStack;

    @Column(name = "team_member")
    private String teamMember;

    //모집 마감일
    @Column(name = "deadline",nullable = false)
    @JsonFormat(pattern = "yyyy MM dd")
    private LocalDate deadline;

    @Column(name = "project_url")
    private String projectImg;

    //모집 인원
    @Column(name = "recruitment_count",nullable = false)
    private int recruitmentCount;

    //진행방식
    @Column(name = "study_method",nullable = false)
    private String studyMethod;

    //프로젝트 기간
    @Column(name = "project_duration", nullable = false)
    private String projectDuration;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "view_count")
    private int viewCount;


}