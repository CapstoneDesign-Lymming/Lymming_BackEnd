package com.supernova.lymming.kakao.repository;

import com.supernova.lymming.kakao.entity.KakaoUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KakaoUserRepository extends JpaRepository<KakaoUser,Long> {
    Optional<KakaoUser> findByNickname(String nickname);
    Optional<KakaoUser> findByServerNickname(String serverNicname);


}
