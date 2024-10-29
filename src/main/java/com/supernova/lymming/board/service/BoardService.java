package com.supernova.lymming.board.service;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BoardService {
    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public BoardDto createBoard(BoardDto boardDto) {
        // 새로운 게시판 생성
        BoardEntity board = new BoardEntity();
        log.info("게시글 작성 요청이 들어옴");

        // 필드 값 설정
        board.setTitle(boardDto.getTitle());
        log.info("title: {}", boardDto.getTitle());

        board.setContent(boardDto.getContent());
        log.info("content: {}", boardDto.getContent());

        // 추가 필드 값 설정
        board.setCategory(boardDto.getCategory());
        board.setRecruitmentCount(boardDto.getRecruitmentCount());
        board.setProjectMethod(boardDto.getProjectMethod());
        board.setProjectDuration(boardDto.getProjectDuration());
        board.setRecruitmentDeadline(boardDto.getRecruitmentDeadline());
        board.setPosition(boardDto.getPosition());
        board.setDevelopmentStyle(boardDto.getDevelopmentStyle());

        // 작성자 정보 및 프로젝트 ID 설정
        board.setUserId(boardDto.getUserId()); // 현재 사용자의 ID 설정
        board.setProjectId(boardDto.getProjectId()); // 프로젝트 ID 설정

        // 게시판 저장
        boardRepository.save(board);
        log.info("게시글 저장됨: {}", board);

        return boardDto;
    }

    public List<BoardDto> getBoardList() {
        List<BoardEntity> boardList = boardRepository.findAll();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (BoardEntity board : boardList) {
            BoardDto boardDto = new BoardDto(
                    board.getProjectId(),
                    board.getUserId(),
                    board.getTitle(),
                    board.getContent(),
                    board.getCategory(),
                    board.getRecruitmentCount(),
                    board.getProjectMethod(),
                    board.getProjectDuration(),
                    board.getRecruitmentDeadline(),
                    board.getPosition(),
                    board.getDevelopmentStyle()
            );
            boardDtoList.add(boardDto);
        }

        return boardDtoList;
    }

    public BoardDto update(Integer id, BoardDto boardDto) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항이 없습니다."));

        // 게시글 업데이트
        board.setTitle(boardDto.getTitle());
        board.setContent(boardDto.getContent());
        boardRepository.save(board);

        return boardDto;
    }
}
