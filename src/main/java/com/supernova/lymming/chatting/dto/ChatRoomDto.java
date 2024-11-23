package com.supernova.lymming.chatting.dto;

import com.supernova.lymming.chatting.domain.ChatMessage;
import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.github.entity.User;
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
    private ChatMessage lastMessage;
    UserChatRooms chatRoom;
    private String user1Img;
    private String user2Img;


    public ChatRoomDto() {
    }


    public ChatRoomDto(String roomId, String userId1, String userId2, String user1Img, String user2Img,ChatMessage lastMessage) {
        this.roomId = roomId;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.user1Img = user1Img;
        this.user2Img = user2Img;
        this.lastMessage = lastMessage;
    }


    public ChatRoomDto(String roomId, String userId1, String userId2, ChatMessage lastMessage) {
        this.roomId = roomId;
        this.userId1 = userId1;
        this.userId2 = userId2;
        this.lastMessage = lastMessage;
    }




}
