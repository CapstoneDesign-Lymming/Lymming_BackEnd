package com.supernova.lymming.chatting.controller;

import com.supernova.lymming.chatting.domain.ChatRoomDto;
import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.chatting.repository.ChatRoomRepository;
import com.supernova.lymming.chatting.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService ;


    @PostMapping("/chat/room/create")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        String userId = request.get("userId");

        // 요청 데이터 로그 출력
        System.out.println("Received roomId: " + roomId + ", userId: " + userId);

        ChatRoomDto chatRoom =chatRoomService.createChatRoom(roomId, userId);
        return ResponseEntity.ok(chatRoom);
    }


    @PostMapping("chat/existroom")
    public ResponseEntity<ChatRoomDto> existChatRoom(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        ChatRoomDto chatRoom = chatRoomService.getChatRoomByRoomId(roomId);
        return ResponseEntity.ok(chatRoom);
    }

//    @GetMapping(("/api/chatrooms"))
//    public List<ChatRoomDto> getChatroomsByUser(@RequestParam String userId){
//        System.out.println("방 개수"+ chatRoomService.getChatroomsByUser(userId).size());
//        return chatRoomService.getChatroomsByUser(userId);
//    }


}
