package com.supernova.lymming.github.dto;

import com.supernova.lymming.github.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SignupDto {
    private String nickname;
    private String userImg;
    private String position;
//    private List<String> devStyle;
    private String devStyle;
    // 여러개일수도 있어서 리스트 형식으로 수정했습니다
//    private List<String> stack;
    private String stack;
    private Gender gender;
    private String job;
    private Integer developerType;
    private String bio;
    private Integer favorites;
    private Float temperature;
    // 여러개일수도 있어서 리스트 형식으로 수정했습니다
    private String refreshToken;
    private String serverNickname;
    private Long userId;
}