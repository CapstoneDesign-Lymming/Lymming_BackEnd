package com.supernova.lymming.github.auth;

import com.supernova.lymming.github.entity.LoginType;
import com.supernova.lymming.github.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
public class CustomUserDetails implements UserDetails, OAuth2User {
    // Security에서 OAuth2 사용자 정보를 관리하기 위한 클래스

    private Long userId; // DB에 있는 고유한 사용자ID
    private String githubId; // 깃허브에서 가져온 사용자 ID
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public CustomUserDetails(Long userId, String githubId,Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.githubId = githubId;
        this.authorities = authorities;
    }

    public static CustomUserDetails create(User user) {
        // User rorcpfmf rkwlrh CustomUserDetails 객체 생성하고 USER 권한 부여

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUserDetails(
                user.getUserId(),
                user.getGithubId(),
                authorities
        );
    }

    public static CustomUserDetails create(User user, Map<String, Object> attributes) {
        CustomUserDetails userDetails = CustomUserDetails.create(user);
        userDetails.setAttributes(attributes);
        return userDetails;
    }

    @Override
    public String getName() {
        return String.valueOf(userId);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return githubId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
