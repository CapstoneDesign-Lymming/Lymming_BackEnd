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
    private String position;
    private String bio;
    private List<String> devStyle;

    public void setMemberInfo(MemberInfoDto memberInfoDto) {
        this.userId = memberInfoDto.getUserId();
        this.nickname = memberInfoDto.getNickname();
        this.userImg = memberInfoDto.getUserImg();
        this.stack = memberInfoDto.getStack();
        this.job = memberInfoDto.getJob();
        this.position = memberInfoDto.getPosition();
        this.bio = memberInfoDto.getBio();
        this.devStyle = memberInfoDto.getDevStyle();
    }
}
