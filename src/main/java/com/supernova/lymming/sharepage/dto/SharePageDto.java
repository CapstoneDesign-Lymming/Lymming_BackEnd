package com.supernova.lymming.sharepage.dto;

import com.supernova.lymming.sharepage.entity.End;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
public class SharePageDto {
    private Long sharePageId;
    private Long userId;  // User 엔티티의 user_id 값
    private Long projectId;  // BoardEntity 엔티티의 project_id 값
    private String sharePageDescription;
    private String teamMember;
    private String sharePageName;
    private String projectLink;
    private String sharePageUrl;
    private String teamName;
    private End end;

}
