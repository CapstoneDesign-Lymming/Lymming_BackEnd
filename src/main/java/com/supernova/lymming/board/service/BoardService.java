package com.supernova.lymming.board.service;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository; // UserRepository 추가

    @Autowired
    public BoardService(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
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
        board.setProjectImg(boardDto.getProjectImg());
        board.setRecruitmentCount(boardDto.getRecruitmentCount());
        board.setStudyMethod(boardDto.getStudyMethod());
        board.setProjectDuration(boardDto.getProjectDuration());
        board.setProjectName(boardDto.getProjectName());

        // 사용자 조회 후 설정
        User user = userRepository.findById(boardDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        board.setUser(user); // User 엔티티 설정

        // 게시판 저장
        boardRepository.save(board);
        log.info("게시글 저장됨: {}", board);

//        // SharePageEntity 생성 및 저장
//        SharePageEntity sharePage = new SharePageEntity();
//        sharePage.setUser(user);  // User 객체 설정
//        sharePage.setBoard(board);  // 생성된 BoardEntity와 연결
//
//        sharePageRepository.save(sharePage);
//        log.info("SharePage 생성됨: {}", sharePage);

        return boardDto;
    }

    public List<BoardDto> getBoardList() {
        List<BoardEntity> boardList = boardRepository.findAll();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (BoardEntity board : boardList) {
            Long userId = board.getUser().getUserId();  // UserId는 BoardEntity의 User 객체에서 가져오기

            System.out.println("유저 아이디: "+ userId);
            System.out.println("유저 아이디: "+ board.getUser().getNickname());

            BoardDto boardDto = new BoardDto(
                    board.getProjectId(),
                    userId,  // 수정된 부분: board.getUser().getUserId()로 UserId를 가져옴
                    board.getUser().getNickname(),
                    board.getStudyType(),
                    board.getUploadTime(),
                    board.getRecruitmentField(),
                    board.getDescription(),
                    board.getWorkType(),
                    board.getTechStack(),
                    board.getDeadline(),
                    board.getProjectImg(),
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

    public BoardDto getBoardById(Long projectId) {
        BoardEntity board = boardRepository.findByProjectId(projectId)
                .orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다."));

        Long userId = board.getUser().getUserId();  // UserId는 BoardEntity의 User 객체에서 가져오기

        return new BoardDto(
                board.getProjectId(),
                userId,
                board.getUser().getNickname(),// 수정된 부분: board.getUser().getUserId()로 UserId를 가져옴
                board.getStudyType(),
                board.getUploadTime(),
                board.getRecruitmentField(),

                board.getDescription(),
                board.getWorkType(),
                board.getTechStack(),
                board.getDeadline(),
                board.getProjectImg(),
                board.getRecruitmentCount(),
                board.getStudyMethod(),
                board.getProjectDuration(),
                board.getProjectName()
        );
    }
}
