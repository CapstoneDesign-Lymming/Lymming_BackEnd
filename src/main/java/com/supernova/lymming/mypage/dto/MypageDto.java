package com.supernova.lymming.mypage.dto;

import lombok.Data;
import java.util.List;

@Data
public class MypageDto {

    private Long userId;
    private String nickname;
    private String userImg;
    private List<String> stack;
    private String job;
    private String position;
    private List<String> devStyle;
    private float temperature;
}
