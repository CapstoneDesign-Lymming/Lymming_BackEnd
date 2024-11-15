package com.supernova.lymming.member.repository;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.member.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberInfo, Long> {

    Optional<MemberInfo> findByBoard_ProjectId(Long projectId);
    List<MemberInfo> findByUser_userId(Long userId);
}

