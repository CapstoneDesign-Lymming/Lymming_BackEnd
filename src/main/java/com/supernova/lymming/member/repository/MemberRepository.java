package com.supernova.lymming.member.repository;

import com.supernova.lymming.member.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberInfo, Long> {
//    // projectId로 MyPageEntity 조회
//    Optional<MemberInfo> findByProjectId(Long projectId);
}
