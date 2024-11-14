package com.supernova.lymming.heart.controller;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.heart.service.HeartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HeartController {
    private final HeartService heartService;

    @Autowired
    public HeartController(HeartService heartService) {
        this.heartService = heartService;
    }

    // 좋아요 누르기
    @PostMapping("/{userId}/likes/{projectId}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<String> likeProject(@PathVariable Long userId, @PathVariable Long projectId) {
        heartService.likeProject(userId, projectId);
        return ResponseEntity.ok("Project liked successfully");
    }

    // 좋아요 취소
    @DeleteMapping("/{userId}/likes/{projectId}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<String> unlikeProject(@PathVariable Long userId, @PathVariable Long projectId) {
        heartService.unlikeProject(userId, projectId);
        return ResponseEntity.ok("Project unliked successfully");
    }

    // 사용자가 좋아요한 게시물 목록 조회
    @GetMapping("/favorites/list")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<BoardEntity>> getLikedProjects(@PathVariable Long userId) {
        List<BoardEntity> likedProjects = heartService.getLikeProjects(userId);
        return ResponseEntity.ok(likedProjects);
    }
}
