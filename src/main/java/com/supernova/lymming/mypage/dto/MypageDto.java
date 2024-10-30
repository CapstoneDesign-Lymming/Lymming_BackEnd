package com.supernova.lymming.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MypageDto {

    private Integer mypageId;
    private Integer userId;
    private Integer projectId;
    private Integer sharePageId;
    private String nickName;
    private String memberId;
    private Integer password;
    private String job;
    private String position;
    private String spac;
    private int temp;
    private String myImage;

}
