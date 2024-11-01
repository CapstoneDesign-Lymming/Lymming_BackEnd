package com.supernova.lymming.chatting.dto;

import lombok.Data;

@Data

public class ChatRoomDto {

    private Long id;
    private String roomId;
    private String userId;

    public ChatRoomDto() {
    }


    public ChatRoomDto(String roomId, String userId) {
        this.roomId = roomId;
        this.userId = userId;
    }


    // getRoomId 메서드를 명시적으로 정의
    public String getRoomId() {
        return roomId;
    }
}
