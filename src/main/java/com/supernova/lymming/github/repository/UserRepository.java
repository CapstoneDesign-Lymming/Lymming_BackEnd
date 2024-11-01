package com.supernova.lymming.github.repository;

import com.supernova.lymming.github.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByGithubId(String githubId);

    @Modifying
    @Query(value="UPDATE User u SET u.refreshToken=:token WHERE u.userId=:userId")
    @Transactional
    void updateRefreshToken(@Param("userId") Long userId, @Param("token") String refreshToken);

    @Query("Select u.refreshToken FROM User u WHERE u.userId=:userId") // :userId로 수정
    String getRefreshToken(@Param("userId") Long userId); // 매개변수 타입도 Long으로 유지
}
