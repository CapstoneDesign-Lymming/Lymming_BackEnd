package com.supernova.lymming.sharepage.Entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="sharepage")
@Data
public class SharePageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer sharePageId;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String projectId;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private String content;

    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;
    private String imageUrl4;

    @Column(nullable = false)
    private String teamMember;

    @Column(nullable = false)
    private String projectLink;

}

