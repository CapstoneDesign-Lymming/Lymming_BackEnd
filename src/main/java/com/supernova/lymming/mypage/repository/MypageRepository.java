package com.supernova.lymming.mypage.repository;

import com.supernova.lymming.member.entity.MemberInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MypageRepository extends JpaRepository <MemberInfo, Long> {
//    Optional<MemberInfo> findByUser_UserId(Long userId);
}
