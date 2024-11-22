package com.supernova.lymming.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoDetailDto {
    private Long userId;
    private String nickname;
    private String userImg;
    private List<String> devStyle;
    private Float temperature;
    private String bio;
    private String job;
    private String position;

}