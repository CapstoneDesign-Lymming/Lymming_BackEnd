package com.supernova.lymming.sharepage.repository;

import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.sharepage.entity.SharePageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharePageRepository extends JpaRepository <SharePageEntity,Long> {
    Optional<SharePageEntity> findBySharePageId(Long sharePageId);
}
