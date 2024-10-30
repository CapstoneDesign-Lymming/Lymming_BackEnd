package com.supernova.lymming.sharepage.Repository;

import com.supernova.lymming.sharepage.Entity.SharePageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharePageRepository extends JpaRepository <SharePageEntity,Integer> {
    List<SharePageEntity> findByUserId(Integer userId);
}
