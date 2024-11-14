package com.supernova.lymming.chatting.service;

import com.supernova.lymming.chatting.domain.ChatMessage;
import com.supernova.lymming.chatting.repository.ChatRepository;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChattingService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRepository chatRepository;

    public ChattingService(SimpMessagingTemplate messagingTemplate, ChatRepository chatRepository) {
        this.messagingTemplate = messagingTemplate;
        this.chatRepository = chatRepository;
    }


    public List<ChatMessage> getChatHistory(String roomId) {
        System.out.println("채팅 기록");
        System.out.println("방번호" + roomId);

        List<ChatMessage> arr = chatRepository.findByRoomId(roomId);
        System.out.println(arr.size());
        return chatRepository.findByRoomId(roomId);
    }

    public void handleMessage(ChatMessage message) {
        // 메시지 도착 로그
        System.out.println("메세지 도착: " + message.getContent());

        chatRepository.save(message);
        // 메시지를 "/sub/room"으로 브로드캐스트
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        System.out.println("방 아이디: "+message.getRoomId());
        // 브로드캐스트 성공 로그
        System.out.println("메시지 전송 성공: " + message.getContent());
    }
}
