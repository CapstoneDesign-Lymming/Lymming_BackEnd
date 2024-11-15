package com.supernova.lymming.board.repository;

import com.supernova.lymming.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

   Optional<BoardEntity> findByProjectId(Long projectId);

   @Modifying
   @Query("update BoardEntity b set b.viewCount = b.viewCount + 1 where b.projectId = :projectId")
   void updateCount(Long projectId);
}

