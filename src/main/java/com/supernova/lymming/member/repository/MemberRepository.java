package com.supernova.lymming.member.repository;

import com.supernova.lymming.github.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<User, Long> {
    Optional<User> findByNickname(String nickname);
}
