package com.supernova.lymming.chatting.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChatRoomDto {

    private Long id;
    private String roomId;
    private String userId1;
    private String userId2;

    public ChatRoomDto() {
    }


    public ChatRoomDto(String roomId, String userId1,String userId2) {
        this.roomId = roomId;
        this.userId1 = userId1;
        this.userId2 = userId2;
    }


}
