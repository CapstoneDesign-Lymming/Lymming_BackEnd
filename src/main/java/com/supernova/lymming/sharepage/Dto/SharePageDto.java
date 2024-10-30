package com.supernova.lymming.sharepage.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SharePageDto {
    private Integer sharePageId;
    private Integer userId;
    private String projectId;
    private String teamName;
    private String content;
    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;
    private String imageUrl4;
    private String teamMember;
    private String projectLink;
}


