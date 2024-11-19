package com.supernova.lymming.github.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_type")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

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
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "job")
    private String job;

    @Column(name = "category")
    private String category;

    @Column(name = "bio")
    private String bio;

    @Column(name = "favorites")
    private Integer favorites;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "server_nickname")
    private String serverNickname;

    public User(Long userId, String serverNickname, LoginType loginType) {
        this.userId = userId;
        this.serverNickname = serverNickname;
        this.loginType = loginType;

    }

    // 기본 생성자는 @NoArgsConstructor에 의해 생성됨
    // @AllArgsConstructor가 자동으로 모든 필드를 초기화하는 생성자를 생성
    // @Builder를 통해 빌더 패턴으로도 객체 생성 가능

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", loginType=" + loginType +
                ", githubId='" + serverNickname + '\'' +
                '}';
    }

}