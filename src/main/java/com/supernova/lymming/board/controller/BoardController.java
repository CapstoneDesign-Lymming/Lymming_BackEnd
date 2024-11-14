package com.supernova.lymming.board.controller;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
//@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/teambuild")
    public ResponseEntity<BoardDto> createBoard(@RequestBody BoardDto boardDto) {
        BoardDto newBoard = boardService.createBoard(boardDto);
        return ResponseEntity.ok().body(newBoard);
    }

    @GetMapping("/participate")
    public ResponseEntity<List<BoardDto>> getBoards() {
        List<BoardDto> boards = boardService.getBoardList();
        return ResponseEntity.ok().body(boards);
    }

    @GetMapping("/participate/detail/{projectId}")
    public ResponseEntity<BoardDto> getBoard(@PathVariable Long projectId, HttpServletRequest request, HttpServletResponse response) {
        BoardDto detailBoard = boardService.getBoardById(projectId,request,response);
        return ResponseEntity.ok().body(detailBoard);
    }

    @PutMapping("/projectId")
    public ResponseEntity<BoardDto> updateBoard(@PathVariable Long projectId, @RequestBody BoardDto boardDto) {
        BoardDto updateBorad = boardService.update(Math.toIntExact(projectId),boardDto);
        return ResponseEntity.ok().body(updateBorad);
    }



}


