package com.supernova.lymming.member.repository;

import com.supernova.lymming.github.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository2 extends JpaRepository<User, Long> {
    boolean existsByNickname(String nickname);
}
