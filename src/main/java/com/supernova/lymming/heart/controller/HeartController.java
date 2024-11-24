package com.supernova.lymming.heart.controller;

import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.heart.service.HeartService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "찜 누르기", notes = "찜 누를 시 실행되는 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<String> likeProject(@PathVariable Long userId, @PathVariable Long projectId) {
        heartService.likeProject(userId, projectId);
        return ResponseEntity.ok("Project liked successfully");
    }

    // 좋아요 취소
    @DeleteMapping("/{userId}/likes/{projectId}")
    @ApiOperation(value = "찜 취소", notes = "찜 취소 시 실행되는 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<String> unlikeProject(@PathVariable Long userId, @PathVariable Long projectId) {
        heartService.unlikeProject(userId, projectId);
        return ResponseEntity.ok("Project unliked successfully");
    }

    // 사용자가 좋아요한 게시물 목록 조회
    @GetMapping("/favorites/list/{userId}")
    @ApiOperation(value = "찜 목록 조회", notes = "마이페이지에서 찜한 프로젝트 리스트때 실행되는 API, Token 필요")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<BoardEntity>> getLikedProjects(@PathVariable Long userId) {
        List<BoardEntity> likedProjects = heartService.getLikeProjects(userId);
        return ResponseEntity.ok(likedProjects);
    }
}
