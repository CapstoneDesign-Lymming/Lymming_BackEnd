package com.supernova.lymming.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
public class MemberInfoDto {

    private Long userId;
    private String nickname;
    private String userImg;
    private List<String> stack;
    private String job;
    private String bio;
    private String position;
    private List<String> devStyle;
    private Float temperature;
    private List<String> projectNames; // 여러 개의 프로젝트 이름
    private List<LocalDate> deadlines; // 여러 개의 마감일
}
