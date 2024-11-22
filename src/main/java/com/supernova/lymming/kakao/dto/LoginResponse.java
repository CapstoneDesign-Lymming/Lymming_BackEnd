package com.supernova.lymming.kakao.dto;

import com.supernova.lymming.github.entity.Gender;
import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Long userId;
    private String nickname;
    private String userImg;
    private String position;
    private String devStyle;
    private String stack;
    private String gender;
    private String job;
    private Integer developerType;
    private String bio;
    private Integer favorites;
    private Float temperature;
    private String interests;
    private String refreshToken;
}
