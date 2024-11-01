package com.supernova.lymming.chatting.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "chatmessage")
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setTimeStamp(String timeStamp){
        this.timestamp=timeStamp;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userid) {
        this.userId = userid;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }


}
