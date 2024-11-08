package com.supernova.lymming.sharepage.Repository;

import com.supernova.lymming.sharepage.Entity.SharePageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SharePageRepository extends JpaRepository <SharePageEntity,Long> {
    Optional<SharePageEntity> findByUser_UserId(Long userId);
    Optional<SharePageEntity> findBySharePageId(Long sharePageId);
}
