package com.supernova.lymming.chatting.service;

import com.supernova.lymming.chatting.dto.ChatRoomDto;
import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.chatting.repository.ChatRoomRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    //private final UserChatRoomsRepository userChatRoomsRepository;


    public List<UserChatRooms> getChatroomsByUserId(String userId1){
        List<UserChatRooms> chatRooms = chatRoomRepository.findByUserId1(userId1);

        System.out.println(chatRooms+"채팅방 개수개수");
        System.out.println(userId1+"유저 아이디");

        if (chatRooms.isEmpty()) {
            // 필요한 경우 예외를 던지거나 빈 리스트를 반환
            return new ArrayList<>(); // 빈 리스트 반환
        }

        return chatRooms;

    }


    public ChatRoomDto getChatRoomByRoomId(String roomId){
        Optional<UserChatRooms> existingRoom = chatRoomRepository.findByRoomId(roomId);
        if (existingRoom.isPresent()) {
            // 채팅방 정보를 가져와 DTO로 변환
            UserChatRooms chatRoom = existingRoom.get();
            return new ChatRoomDto(
                    chatRoom.getRoomId(),
                    chatRoom.getUserId1(),
                    chatRoom.getUserId2()

            );
        }else{
            System.out.println(("채팅방없음"));
            return null;
        }
    }

    public ChatRoomDto createChatRoom(String roomId, String userId1, String userId2) {
        // 중복되는 room_id가 있는지 먼저 확인
        Optional<UserChatRooms> existingRoom = chatRoomRepository.findByRoomId(roomId);


        if (!existingRoom.isPresent()) {
            UserChatRooms userChatRooms = new UserChatRooms(roomId, userId1,userId2);


            chatRoomRepository.save(userChatRooms);
        }

        return new ChatRoomDto(roomId, userId1, userId2);
    }

    public boolean doesChatRoomExist(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).isPresent();
    }
}
