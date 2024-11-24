package com.supernova.lymming.chatting.service;

import com.supernova.lymming.chatting.domain.ChatMessage;
import com.supernova.lymming.chatting.dto.ChatDto;
import com.supernova.lymming.chatting.dto.ChatRoomDto;
import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.chatting.repository.ChatRoomRepository;

import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChattingService chattingService;
    private final UserRepository userRepository;

    public List<ChatRoomDto> getChatroomsByUserId(String userId1) {

        List<UserChatRooms> chatRooms = chatRoomRepository.findChatroomsByUserId1(userId1);


        return chatRooms.stream()
                .map(chatRoom -> {
                    // userId1과 userId2에 해당하는 사용자 이미지 조회
                    Optional<User> user1Opt = userRepository.findByNickname(chatRoom.getUserId1());
                    Optional<User> user2Opt = userRepository.findByNickname(chatRoom.getUserId2());

                    String user1Img = user1Opt.map(User::getUserImg).orElse(null); // 없으면 null
                    String user2Img = user2Opt.map(User::getUserImg).orElse(null); // 없으면 null

                    ChatMessage lastMessage = getLastChatByRoomId(chatRoom.getRoomId());
                    // ChatRoomDto 객체로 변환하면서 이미지 추가
                    return new ChatRoomDto(
                            chatRoom.getRoomId(),
                            chatRoom.getUserId1(),
                            chatRoom.getUserId2(),
                            user1Img,
                            user2Img,
                            lastMessage
                    );
                })
                .collect(Collectors.toList());

    }

    // 마지막 채팅 추출
    public ChatMessage getLastChatByRoomId(String roomId) {
        List<ChatMessage> chatData = chattingService.getChatHistory(roomId);
//        System.out.println(chatData);
        if (chatData.isEmpty()) {
            return null;
        }

        ChatMessage chatDto = chatData.get(chatData.size() - 1);

        return chatDto;
    }


    public ChatRoomDto getChatRoomByRoomId(String roomId) {
        Optional<UserChatRooms> existingRoom = chatRoomRepository.findByRoomId(roomId);
        if (existingRoom.isPresent()) {
            UserChatRooms chatRoom = existingRoom.get();
            // userId1과 userId2를 사용하여 User 조회
            Optional<User> user1Opt = userRepository.findByNickname(chatRoom.getUserId1());
            Optional<User> user2Opt = userRepository.findByNickname(chatRoom.getUserId2());
            ChatMessage lastMessage = getLastChatByRoomId(chatRoom.getRoomId());

            // 각 사용자 이미지 설정
            String user1Img = user1Opt.map(User::getUserImg).orElse(null);  // user1이 존재하면 userImg 반환, 없으면 null
            String user2Img = user2Opt.map(User::getUserImg).orElse(null);  // user2이 존재하면 userImg 반환, 없으면 null
//  System.out.println("사용자 이미지"+user1Img);

            // 채팅방 정보를 가져와 DTO로 변환
            return new ChatRoomDto(
                    chatRoom.getRoomId(),
                    chatRoom.getUserId1(),
                    chatRoom.getUserId2(),
                    user1Img,
                    user2Img,
                    lastMessage

            );
        } else {
//            System.out.println(("채팅방없음"));
            return null;
        }
    }

    public ChatRoomDto createChatRoom(String roomId, String userId1, String userId2, String user1Img, String user2Img) {
        // 중복되는 room_id가 있는지 먼저 확인
        Optional<UserChatRooms> existingRoom = chatRoomRepository.findByRoomId(roomId);


        if (!existingRoom.isPresent()) {
            UserChatRooms userChatRooms = new UserChatRooms(roomId, userId1, userId2);


            chatRoomRepository.save(userChatRooms);
        }

        return new ChatRoomDto(roomId, userId1, userId2, user1Img, user2Img,null);
    }

    public boolean doesChatRoomExist(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).isPresent();
    }
}
