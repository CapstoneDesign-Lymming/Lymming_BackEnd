package com.supernova.lymming.kakao.entity;

import com.supernova.lymming.github.entity.Gender;
import com.supernova.lymming.github.entity.LoginType;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class KakaoUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "server_nickname")
    private String serverNickname;

    @Column(name = "login_type")
    private String loginType;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "position")
    private String position;

    @Column(name = "dev_style")
    private String devStyle;

    @Column(name = "user_img")
    private String userImg;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "stack")
    private String stack;

    @Column(name = "gender")
    private String gender;

    @Column(name = "job")
    private String job;

    @Column(name = "developer_type")
    private Integer developer_type;

    @Column(name = "bio")
    private String bio;

    @Column(name = "favorites")
    private Integer favorites;

    @Column(name = "temperature")
    @Builder.Default
    private Float temperature = 36.5f;

    @Column(name = "interests")
    private String interests;

    @ElementCollection(fetch = FetchType.EAGER) //roles 컬렉션
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}