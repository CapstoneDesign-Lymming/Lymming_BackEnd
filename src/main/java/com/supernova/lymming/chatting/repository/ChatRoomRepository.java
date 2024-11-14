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

    @Query("SELECT c FROM UserChatRooms c WHERE c.userId1 = :userId1 OR c.userId2 = :userId1")
    List<UserChatRooms> findChatroomsByUserId1(@Param("userId1") String userId1);


}
