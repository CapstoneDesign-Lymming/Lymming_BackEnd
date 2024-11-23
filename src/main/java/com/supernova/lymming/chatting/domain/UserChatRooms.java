package com.supernova.lymming.chatting.domain;


import com.supernova.lymming.github.entity.User;
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

    @Column(name = "user_id1")
    private String userId1;
    @Column(name = "user_id2")
    private String userId2;

    @Column(name = "room_id")
    private String roomId;


    // 기본 생성자
    public UserChatRooms() {}


    public UserChatRooms(String roomId,String userId1, String userId2) {
        this.roomId = roomId;
        this.userId1 = userId1;
        this.userId2 = userId2;


    }


}
