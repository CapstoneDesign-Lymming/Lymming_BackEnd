package com.supernova.lymming.chatting.controller;

import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.chatting.dto.ChatRoomDto;
import com.supernova.lymming.chatting.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @RestController
    @RequestMapping("/chatting")
    public class InfoController {
        @GetMapping("/info")
        public ResponseEntity<String> info() {
            return ResponseEntity.ok("WebSocket info endpoint");
        }
    }



    @PostMapping("/chat/room/create")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        String userId1 = request.get("userId1");
        String userId2 = request.get("userId2");


        ChatRoomDto chatRoom = chatRoomService.createChatRoom(roomId, userId1,userId2);
        return ResponseEntity.ok(chatRoom);
    }


    @PostMapping("chat/existroom")
    public ResponseEntity<ChatRoomDto> existChatRoom(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        ChatRoomDto chatRoom = chatRoomService.getChatRoomByRoomId(roomId);


        return ResponseEntity.ok(chatRoom);


    }


    @GetMapping("/chat/chatrooms")
    public List<ChatRoomDto> getChatroomsByUserId(@RequestParam String userId) {

        List<ChatRoomDto> chatRooms = chatRoomService.getChatroomsByUserId(userId);

        return chatRooms;
    }


}
