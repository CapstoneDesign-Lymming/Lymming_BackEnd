package com.supernova.lymming.member.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MemberInfoDto {

    private Long userId;
    private String nickname;
    private String userImg;
    private List<String> stack;
    private String job;
    private String position;
    private List<String> devStyle;
    private float temperature;
    private Long projectId;
    private String projectName;
    private LocalDate deadline;
}
