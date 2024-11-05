package com.supernova.lymming.chatting.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
@Table(name = "chatmessage")
@Setter
@Getter
public class ChatMessage {

//    // 메세지에 관한 객체
//    public enum MessageType {
//        // 메세지 형식
//        ENTER, TALK, EXIT, MATCH, MATCH_REQUEST;
//    }
    // 메세지 타입, 방번호, 송신자, 메세지 내용
    //private MessageType type;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "roomId")
    private String roomId;
    @Column(name = "userId")
    private String userId;
    @Column(name = "userName")
    private String userName;
    @Column(name = "timestamp")
    private String timestamp;
    @Column(name = "content")
    private String content;


}