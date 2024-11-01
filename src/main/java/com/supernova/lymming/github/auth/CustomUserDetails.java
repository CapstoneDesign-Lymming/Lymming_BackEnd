package com.supernova.lymming.github.auth;

import com.supernova.lymming.github.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomUserDetails implements UserDetails {

    private final User user; // 사용자 정보를 담고 있는 User 객체
    private final Collection<? extends GrantedAuthority> authorities; // 사용자 권한

    // 기존 생성자
    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = Collections.emptyList(); // 기본적으로 빈 권한 리스트로 초기화
    }

    // 수정된 생성자
    public CustomUserDetails(Long userId, String githubId, Collection<? extends GrantedAuthority> authorities) {
        this.user = new User(userId, githubId); // User 객체를 id와 githubId로 초기화
        this.authorities = authorities; // 권한 설정
    }

    public static CustomUserDetails create(User user) {
        return new CustomUserDetails(user);
    }

    public static CustomUserDetails create(User user, Map<String, Object> attributes) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return userDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // 설정된 권한을 반환
    }

    @Override
    public String getPassword() {
        return null; // 비밀번호가 없으므로 null 반환
    }

    @Override
    public String getUsername() {
        return user.getGithubId(); // GitHub ID를 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화 여부
    }

    public User getUser() {
        return user; // User 객체 반환
    }
}
