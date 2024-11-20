package com.supernova.lymming.sharepage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareUserInfoDto {
    private Long sharePageId;
    private String nickname;
}
