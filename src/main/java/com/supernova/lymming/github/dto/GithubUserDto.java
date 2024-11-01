package com.supernova.lymming.github.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GithubUserDto {
    private String githubId;
    private String nickname;
    private String userImg;
    private String position;
    private String devStyle;
    private String stack;
    private String gender;
    private String job;
    private String category;
    private String bio;
    private Integer favorites;
    private Float temperature;
    private String interests;
}
