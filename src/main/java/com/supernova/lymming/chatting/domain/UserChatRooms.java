package com.supernova.lymming.chatting.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "chatroom")
@Getter
@Setter
public class UserChatRooms implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "room_id")
    private String roomId;

    // 기본 생성자
    public UserChatRooms() {}


    public UserChatRooms(String roomId,String userId) {
        this.roomId = roomId;
        this.userId = userId;

    }


}
