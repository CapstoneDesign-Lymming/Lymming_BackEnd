package com.supernova.lymming.heart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeartDto {
    private Long heartId;
    private Long userId;
    private Long projectId;
    private Boolean isLike;
}
