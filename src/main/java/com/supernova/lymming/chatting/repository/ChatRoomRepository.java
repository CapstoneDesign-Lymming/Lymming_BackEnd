package com.supernova.lymming.chatting.repository;

import com.supernova.lymming.chatting.domain.ChatRoomDto;
import com.supernova.lymming.chatting.domain.UserChatRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<UserChatRooms, Long> {
    Optional<UserChatRooms> findByRoomId(String roomId);

}
