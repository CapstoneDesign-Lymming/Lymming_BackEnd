package com.supernova.lymming.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BoardDto {

    private Long projectId;
    private Long userId;
    private String studyType;
    private LocalDate uploadTime;
    private String recruitmentField;
    private String description;
    private String workType;
    private String techStack;
    private LocalDate deadline;
    private String projectImg;
    private int recruitmentCount;
    private String studyMethod;
    private String projectDuration;
    private String projectName;
}
