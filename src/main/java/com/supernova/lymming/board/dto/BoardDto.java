package com.supernova.lymming.board.dto;

import com.supernova.lymming.board.entity.BoardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class BoardDto {

    private Long projectId;
    private Long userId;
    private String studyType;
    private LocalDate uploadTime;
    private String recruitmentField;
    private String description;
    private String userImg;
    private String workType;
    private String techStack;
    private LocalDate deadline;
    private String projectImg;
    private int recruitmentCount;
    private String studyMethod;
    private String projectDuration;
    private String projectName;
    private String nickname;
    private int viewCount;
    private boolean like;

    public BoardDto(BoardEntity boardEntity, boolean like) {
        this.projectId = boardEntity.getProjectId();
        this.userId = boardEntity.getUser().getUserId();
        this.studyType = boardEntity.getStudyType();
        this.uploadTime = boardEntity.getUploadTime();
        this.recruitmentField = boardEntity.getRecruitmentField();
        this.description = boardEntity.getDescription();
        this.userImg = boardEntity.getUser().getUserImg();
        this.workType = boardEntity.getWorkType();
        this.techStack = boardEntity.getTechStack();
        this.deadline = boardEntity.getDeadline();
        this.projectImg = boardEntity.getProjectImg();
        this.recruitmentCount = boardEntity.getRecruitmentCount();
        this.studyMethod = boardEntity.getStudyMethod();
        this.projectDuration = boardEntity.getProjectDuration();
        this.projectName = boardEntity.getProjectName();
        this.nickname = boardEntity.getNickname();
        this.viewCount = boardEntity.getViewCount();
        this.like = like;
    }
}
