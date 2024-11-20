package com.supernova.lymming.sharepage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareTeamAddDto {
    private Long sharePageId;
    private String nickname;
    private String teamMember;
    private String memberUrlBundle;
    private String positionBundle;
}
