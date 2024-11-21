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
    private String bio;
    private List<String> devStyle;
    private float temperature;
    private Long projectId;
    private List<String> projectName;
    private List<LocalDate> deadline;

    public void setMemberInfo(MemberInfoDto memberInfoDto) {
        this.userId = memberInfoDto.getUserId();
        this.nickname = memberInfoDto.getNickname();
        this.userImg = memberInfoDto.getUserImg();
        this.stack = memberInfoDto.getStack();
        this.job = memberInfoDto.getJob();
        this.position = memberInfoDto.getPosition();
        this.devStyle = memberInfoDto.getDevStyle();
        this.temperature = memberInfoDto.getTemperature();
        this.projectId = memberInfoDto.getProjectId();
        this.projectName = memberInfoDto.getProjectName();
        this.deadline = memberInfoDto.getDeadline();
    }

}
