package com.supernova.lymming.chatting.controller;

import com.supernova.lymming.chatting.domain.ChatMessage;
import com.supernova.lymming.chatting.service.ChattingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "https://lymming.link", maxAge = 3600)

@RestController
public class ChattingController {

    private final ChattingService chattingService;

    @Autowired
    public ChattingController(ChattingService chattingService){
        this.chattingService = chattingService;
    }


    @GetMapping("/chat/{roomId}/history")
    public List<ChatMessage> getMessage(@PathVariable String roomId) {
        return chattingService.getChatHistory(roomId);
    }



    @MessageMapping("/chatting/message")
    public void sendMessage(ChatMessage message) {
        chattingService.handleMessage(message);
    }
}
