package com.supernova.lymming.mypage.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name ="mypage")
public class MypageEntity {

    @Id
    @Column(nullable = false)
    private Integer mypageId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private Integer projectId;

    @Column(nullable = false)
    private Integer sharePageId;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private Integer password;

    @Column(nullable = false)
    private String job;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String spac;

    @Column(nullable = false)
    private int temp;

    @Column(nullable = false)
    private String myImage;
}
