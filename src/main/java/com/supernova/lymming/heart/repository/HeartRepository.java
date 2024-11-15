package com.supernova.lymming.heart.repository;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.heart.entity.HeartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  HeartRepository extends JpaRepository <HeartEntity, Long> {
    boolean existsByUserIdAndProjectId(User user, BoardEntity project);
    Optional<HeartEntity> findByUserIdAndProjectId(User user, BoardEntity project);
    List<HeartEntity> findAllByUserId(User user);
}
