package com.supernova.lymming.chatting.service;

import com.supernova.lymming.chatting.domain.ChatMessage;
import com.supernova.lymming.chatting.dto.ChatDto;
import com.supernova.lymming.chatting.dto.ChatRoomDto;
import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.chatting.repository.ChatRoomRepository;

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


    public List<ChatRoomDto> getChatroomsByUserId(String userId1) {
        List<UserChatRooms> chatRooms = chatRoomRepository.findByUserId1(userId1);


        return chatRooms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }

    private ChatRoomDto convertToDto(UserChatRooms userChatRooms) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setRoomId(userChatRooms.getRoomId());
        dto.setUserId1(userChatRooms.getUserId1());
        dto.setUserId2(userChatRooms.getUserId2());

        // 마지막 채팅 메시지 설정
        ChatMessage lastChat = getLastChatByRoomId(userChatRooms.getRoomId());
        dto.setLastMessage(lastChat != null ? lastChat : null);

        return dto;
    }

    // 마지막 채팅 추출
    public ChatMessage getLastChatByRoomId(String roomId) {
        List<ChatMessage> chatData = chattingService.getChatHistory(roomId);
        System.out.println(chatData);
        if (chatData.isEmpty()) {
            return null;
        }

        ChatMessage chatDto = chatData.get(chatData.size() - 1);

        return chatDto;
    }


    public ChatRoomDto getChatRoomByRoomId(String roomId) {
        Optional<UserChatRooms> existingRoom = chatRoomRepository.findByRoomId(roomId);
        if (existingRoom.isPresent()) {
            // 채팅방 정보를 가져와 DTO로 변환
            UserChatRooms chatRoom = existingRoom.get();
            return new ChatRoomDto(
                    chatRoom.getRoomId(),
                    chatRoom.getUserId1(),
                    chatRoom.getUserId2()

            );
        } else {
            System.out.println(("채팅방없음"));
            return null;
        }
    }

    public ChatRoomDto createChatRoom(String roomId, String userId1, String userId2) {
        // 중복되는 room_id가 있는지 먼저 확인
        Optional<UserChatRooms> existingRoom = chatRoomRepository.findByRoomId(roomId);


        if (!existingRoom.isPresent()) {
            UserChatRooms userChatRooms = new UserChatRooms(roomId, userId1, userId2);


            chatRoomRepository.save(userChatRooms);
        }

        return new ChatRoomDto(roomId, userId1, userId2);
    }

    public boolean doesChatRoomExist(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).isPresent();
    }
}
