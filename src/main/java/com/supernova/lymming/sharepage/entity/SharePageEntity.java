package com.supernova.lymming.sharepage.entity;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.github.entity.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="sharepage")
@Data
public class SharePageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_page_id")
    private Long sharePageId;

    @ManyToOne  // User 엔티티와의 관계 설정 , referenceedColumnName은 테이블의 참조할 컬럼명
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false )
    private BoardEntity board;

    @Column(name = "share_page_description")
    private String sharePageDescription;

    @Column(name = "team_member")
    private String teamMember;

    @Column(name = "share_page_name")
    private String sharePageName;

    @Column(name = "end")
    @Enumerated(EnumType.STRING)
    private End end;

    @Column(name = "project_link")
    private String projectLink;

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "share_page_url")
    private String sharePageUrl;

    @Column(name = "leader")
    private String leader;

    @Column(name = "member_url_bundle")
    private String memberUrlBundle;

    @Column(name = "position_bundle")
    private String positionBundle;

}
