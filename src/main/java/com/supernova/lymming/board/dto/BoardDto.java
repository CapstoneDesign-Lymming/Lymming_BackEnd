package com.supernova.lymming.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardDto {

    private Integer projectId;
    private Integer userId;
    private String title;
    private String content;
    private String category;
    private String recruitmentCount;
    private String projectMethod;
    private String projectDuration;
    private String recruitmentDeadline;
    private String position;
    private String developmentStyle;
}
