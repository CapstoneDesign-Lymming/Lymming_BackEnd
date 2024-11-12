package com.supernova.lymming.board.controller;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.service.BoardService;
import io.swagger.annotations.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/teambuild")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<BoardDto> createBoard(@RequestBody BoardDto boardDto) {
        BoardDto newBoard = boardService.createBoard(boardDto);
        return ResponseEntity.ok().body(newBoard);
    }

    @GetMapping("/participate")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<List<BoardDto>> getBoards() {
        List<BoardDto> boards = boardService.getBoardList();
        return ResponseEntity.ok().body(boards);
    }

    @GetMapping("/participate/{projectId}")
    @CrossOrigin(origins = "https://lymming.link", maxAge = 3600)
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long projectId) {
        BoardDto detailBoard = boardService.getBoardById(projectId);
        return ResponseEntity.ok().body(detailBoard);
    }


}


