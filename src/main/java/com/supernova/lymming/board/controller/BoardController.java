package com.supernova.lymming.board.controller;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    //프로젝트 생성
    @PostMapping("/teambuild")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<BoardDto> createBoard(@RequestBody BoardDto boardDto) {
        BoardDto newBoard = boardService.createBoard(boardDto);
        return ResponseEntity.ok().body(newBoard);
    }

    //프로젝트 리스트
    @GetMapping("/participate")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<List<BoardDto>> getBoards() {
        List<BoardDto> boards = boardService.getBoardList();
        return ResponseEntity.ok().body(boards);
    }

    //프로젝트 상세보기
    @GetMapping("/participate/detail/{projectId}")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long projectId, HttpServletRequest request, HttpServletResponse response) {
        BoardDto detailBoard = boardService.getBoardById(projectId,request,response);
        return ResponseEntity.ok().body(detailBoard);
    }

    //프로젝트 수정
    @PutMapping("/{projectId}")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<BoardDto> updateBoard(@PathVariable Long projectId, @RequestBody BoardDto boardDto) {
        BoardDto updateBorad = boardService.update(projectId,boardDto);
        return ResponseEntity.ok().body(updateBorad);
    }

    // 로그인 후 보이는 참여하기 게시판 목록
    @GetMapping("/participate/{userId}")
    @CrossOrigin(origins = {"https://lymming.link", "https://lymming-back.link"}, maxAge = 3600)
    public ResponseEntity<List<BoardDto>> getBoards(@PathVariable Long userId) {
        List<BoardDto> boards = boardService.getBoardsWithHearts(userId);
        return ResponseEntity.ok().body(boards);
    }

    @GetMapping("/list/project/{userId}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<BoardDto>> getUserProject(@PathVariable Long userId) {
        List<BoardDto> userProject = boardService.getUserProject(userId);
        return ResponseEntity.ok(userProject);
    }

}


