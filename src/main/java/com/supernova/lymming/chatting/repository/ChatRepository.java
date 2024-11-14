package com.supernova.lymming.chatting.repository;

import com.supernova.lymming.chatting.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage,Long> {

    List<ChatMessage> findByRoomId(String roomId);


}
