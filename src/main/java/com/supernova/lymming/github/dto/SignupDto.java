package com.supernova.lymming.github.dto;

import com.supernova.lymming.github.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SignupDto {
    private String nickname;
    private String userImg;
    private String position;
    private String devStyle;
    private String stack;
    private Gender gender;
    private String job;
    private String category;
    private String bio;
    private Integer favorites;
    private Float temperature;
    private String interests;
}


