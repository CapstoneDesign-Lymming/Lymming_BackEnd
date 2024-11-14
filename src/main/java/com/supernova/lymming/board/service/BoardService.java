package com.supernova.lymming.board.service;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
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
        board.setProjectName(boardDto.getProjectName());
        log.info("projectName: {}", boardDto.getProjectName());

        board.setDescription(boardDto.getDescription());
        log.info("Descriptiont: {}", boardDto.getDescription());

        // 추가 필드 값 설정
        board.setStudyType(boardDto.getStudyType());
        board.setUploadTime(boardDto.getUploadTime());
        board.setRecruitmentField(boardDto.getRecruitmentField());
        board.setDescription(boardDto.getDescription());
        board.setWorkType(boardDto.getWorkType());
        board.setTechStack(boardDto.getTechStack());
        board.setDeadline(boardDto.getDeadline());
        board.setViewCount(board.getViewCount());
        board.setRecruitmentCount(board.getRecruitmentCount());
        board.setStudyMethod(boardDto.getStudyMethod());
        board.setProjectDuration(boardDto.getProjectDuration());
        board.setProjectName(boardDto.getProjectName());
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
                    board.getStudyType(),
                    board.getUploadTime(),
                    board.getRecruitmentField(),
                    board.getDescription(),
                    board.getWorkType(),
                    board.getTechStack(),
                    board.getDeadline(),
                    board.getViewCount(),
                    board.getRecruitmentCount(),
                    board.getStudyMethod(),
                    board.getProjectDuration(),
                    board.getProjectName()
            );
            boardDtoList.add(boardDto);
        }

        return boardDtoList;
    }

    public BoardDto update(Integer id, BoardDto boardDto) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 없습니다."));

        // 게시글 업데이트
        board.setProjectName(boardDto.getProjectName());
        board.setDescription(boardDto.getDescription());
        boardRepository.save(board);

        return boardDto;
    }

    @Transactional
    public BoardDto getBoardById(Long projectId,HttpServletRequest request, HttpServletResponse response) {

        BoardEntity board = detail(projectId,request, response);
        return new BoardDto(
                board.getProjectId(),
                board.getUserId(),
                board.getStudyType(),
                board.getUploadTime(),
                board.getRecruitmentField(),
                board.getDescription(),
                board.getWorkType(),
                board.getTechStack(),
                board.getDeadline(),
                board.getViewCount(),
                board.getRecruitmentCount(),
                board.getStudyMethod(),
                board.getProjectDuration(),
                board.getProjectName()
        );
    }

    @Transactional
    public BoardEntity detail(Long projectId, HttpServletRequest request, HttpServletResponse response){
        Cookie oldCookie = null;

        Cookie [] cookies = request.getCookies();

        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("boardView")) {
                    oldCookie = cookie;
                }
            }
        }

        if(oldCookie != null) {
            if(!oldCookie.getValue().contains("["+projectId.toString()+"]")) {
                boardRepository.updateCount(projectId);
                oldCookie.setValue(oldCookie.getValue()+"_["+projectId+"]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        }
        else{
            boardRepository.updateCount(projectId);
            Cookie newCookie = new Cookie("boardView", "[" + projectId + "]");
            newCookie.setMaxAge(60 * 60 * 24);
            newCookie.setPath("/");
            response.addCookie(newCookie);
        }

        return boardRepository.findByProjectId(projectId).orElseThrow(() -> {
            return new IllegalArgumentException("글 상세보기 실패 : 아이디를 찾을 수 없습니다.");
        });
    }
}

