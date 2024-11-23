package com.supernova.lymming.mypage.dto;

import lombok.Data;

@Data
public class MypageDto {

    private Long userId;
    private String nickname;
    private String userImg;
    private String stack;
    private String job;
    private String position;
    private Float temperature;
}
