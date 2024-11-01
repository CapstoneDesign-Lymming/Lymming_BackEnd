package com.supernova.lymming.github.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name="user")
@Getter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long userId;

    @Column(name="login_type")
    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(name="refresh_token", nullable = false)
    private String refreshToken;

    @Column(name="position")
    private String position;

    @Column(name="dev_style")
    private String devStyle;

    @Column(name="user_img")
    private String userImg;

    @Column(name="nickname")
    private String nickname;

    @Column(name="stack")
    private String stack;

    @Column(name="gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name="job")
    private String job;

    @Column(name="category")
    private String category;

    @Column(name="bio")
    private String bio;

    @Column(name="favorites")
    private Integer favorites;

    @Column(name="temperature")
    private Float temperature;

    @Column(name="interests")
    private String interests;

    @Column(name="github_id")
    private String githubId;

    @Builder
    public User(LoginType loginType, String refreshToken, String position, String devStyle,
                String userImg, String nickname, String stack, Gender gender, String job,
                String category, String bio, Integer favorites, Float temperature, String interests, String githubId) {
        this.loginType = loginType;
        this.refreshToken = refreshToken;
        this.position = position;
        this.devStyle = devStyle;
        this.userImg = userImg;
        this.nickname = nickname;
        this.stack = stack;
        this.gender = gender;
        this.job = job;
        this.category = category;
        this.bio = bio;
        this.favorites = favorites;
        this.temperature = temperature;
        this.interests = interests;
        this.githubId = githubId;
    }

    public User(Long userId, String githubId) {
        this.userId = userId;
        this.githubId = githubId;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", loginType=" + loginType +
                ", githubId='" + githubId + '\'' +
                '}';
    }
}
