package com.supernova.lymming.chatting.repository;

import com.supernova.lymming.chatting.domain.UserChatRooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<UserChatRooms, Long> {
    Optional<UserChatRooms> findByRoomId(String roomId);

    List<UserChatRooms> findByUserId1( String userId1);



}
