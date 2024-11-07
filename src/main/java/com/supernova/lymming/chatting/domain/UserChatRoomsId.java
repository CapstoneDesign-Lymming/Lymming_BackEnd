package com.supernova.lymming.chatting.domain;

import java.io.Serializable;

// 복합 키 정의
public class UserChatRoomsId implements Serializable {
    private String userId;
    private String roomId;
}
