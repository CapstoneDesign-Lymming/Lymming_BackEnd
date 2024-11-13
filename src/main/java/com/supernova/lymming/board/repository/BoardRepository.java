package com.supernova.lymming.board.repository;

import com.supernova.lymming.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

   Optional<BoardEntity> findByProjectId(Long projectId);


}

