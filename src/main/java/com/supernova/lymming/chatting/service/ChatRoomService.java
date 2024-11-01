package com.supernova.lymming.chatting.service;

import com.supernova.lymming.chatting.dto.ChatRoomDto;
import com.supernova.lymming.chatting.domain.UserChatRooms;
import com.supernova.lymming.chatting.repository.ChatRoomRepository;
import com.supernova.lymming.chatting.repository.UserChatRoomsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserChatRoomsRepository userChatRoomsRepository;


//    public List<ChatRoomDto> getChatroomsByUser(String userId) {
//        List<UserChatRooms> userChatRooms = userChatRoomsRepository.findByUserId(userId);
//        List<Long> roomIds = userChatRooms.stream()
//                .map(UserChatRooms::getRoomId)
//                .collect(Collectors.toList());
//
//        List<UserChatRooms> chatRooms = chatRoomRepository.findAllById(roomIds);
//
//        // ChatRoom 엔티티를 ChatRoomDto로 변환하여 리스트로 반환
//        return chatRooms.stream()
//                .map(chatRoom -> new ChatRoomDto())
//                .collect(Collectors.toList());
//    }

    public ChatRoomDto getChatRoomByRoomId(String roomId){
        Optional<UserChatRooms> existingRoom = chatRoomRepository.findByRoomId(roomId);
        if (existingRoom.isPresent()) {
            // 채팅방 정보를 가져와 DTO로 변환
            UserChatRooms chatRoom = existingRoom.get();
            return new ChatRoomDto(
                    chatRoom.getRoomId(),
                    chatRoom.getUserId()

            );
        }else{
            System.out.println(("채팅방없음"));
            return null;
        }
    }

    public ChatRoomDto createChatRoom(String roomId, String userId) {
        // 중복되는 room_id가 있는지 먼저 확인
        Optional<UserChatRooms> existingRoom = chatRoomRepository.findByRoomId(roomId);


        if (!existingRoom.isPresent()) {
            UserChatRooms userChatRooms = new UserChatRooms(roomId, userId);


            chatRoomRepository.save(userChatRooms);
        }

        return new ChatRoomDto(roomId, userId);
    }

    public boolean doesChatRoomExist(String roomId) {
        return chatRoomRepository.findByRoomId(roomId).isPresent();
    }
}
