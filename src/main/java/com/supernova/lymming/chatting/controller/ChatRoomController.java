package com.supernova.lymming.chatting.controller;

import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.chatting.dto.ChatRoomDto;
import com.supernova.lymming.chatting.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;


    @PostMapping("/chat/room/create")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        String userId1 = request.get("userId1");
        String userId2 = request.get("userId2");

        // 요청 데이터 로그 출력
        System.out.println("Received roomId: " + roomId + ", userId: " + userId1);

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
    public List<UserChatRooms> getChatroomsByUserId(@RequestParam String userId) {

        List<UserChatRooms> chatRooms = chatRoomService.getChatroomsByUserId(userId);
        System.out.println("방 개수: " + chatRooms.size());
        return chatRooms;
    }


}
