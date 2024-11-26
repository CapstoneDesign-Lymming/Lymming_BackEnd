package com.supernova.lymming.chatting.controller;

import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.chatting.dto.ChatRoomDto;
import com.supernova.lymming.chatting.service.ChatRoomService;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
@RestController
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final UserRepository userRepository;


    @PostMapping("/chat/room/create")
    public ResponseEntity<ChatRoomDto> createChatRoom(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        String userId1 = request.get("userId1");
        String userId2 = request.get("userId2");

        Optional<User> user1Opt = userRepository.findByNickname(userId1);
        Optional<User> user2Opt = userRepository.findByNickname(userId2);

        // 각 사용자 이미지 설정
        String user1Img = user1Opt.map(User::getUserImg).orElse(null);  // user1이 존재하면 userImg 반환, 없으면 null
        String user2Img = user2Opt.map(User::getUserImg).orElse(null);


        ChatRoomDto chatRoom = chatRoomService.createChatRoom(roomId, userId1, userId2, user1Img, user2Img);
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

