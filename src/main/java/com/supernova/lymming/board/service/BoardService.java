package com.supernova.lymming.board.service;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.sharepage.entity.SharePageEntity;
import com.supernova.lymming.sharepage.repository.SharePageRepository;
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
    private final UserRepository userRepository; // UserRepository 추가
    private final SharePageRepository sharePageRepository; // SharePageRepository 추가

    @Autowired
    public BoardService(BoardRepository boardRepository, UserRepository userRepository, SharePageRepository sharePageRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.sharePageRepository = sharePageRepository;
    }

    public BoardDto createBoard(BoardDto boardDto) {
        // 새로운 게시판 생성
        BoardEntity board = new BoardEntity();
        log.info("게시글 작성 요청이 들어옴");

        // 사용자 조회 후 설정
        User user = userRepository.findById(boardDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        board.setUser(user); // User 엔티티 설정

        // User 객체 설정
        board.setUser(user);

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

        log.info("boardDto : {}", boardDto);

        // nickname을 BoardEntity에 설정
        board.setNickname(user.getNickname());
        log.info("board.setNickname : {}",board.getNickname());
        log.info("board.projectName : {}",board.getProjectName());
        log.info("board.deadline : {}",board.getDeadline());
        log.info("board.Description : {}",board.getDescription());


        // 게시판 저장
        boardRepository.save(board);
        log.info("게시글 저장됨: {}", board);

        // SharePageEntity 생성 및 저장
        SharePageEntity sharePage = new SharePageEntity();
        sharePage.setUser(user);  // User 객체 설정
        sharePage.setBoard(board);  // 생성된 BoardEntity와 연결
        sharePage.setLeader(board.getUser().getNickname());

        sharePageRepository.save(sharePage);
        log.info("SharePage 생성됨: {}", sharePage);

        log.info("boardDto : {}", boardDto);

        return boardDto;
    }

    @Transactional
    public List<BoardDto> getBoardList() {
        List<BoardEntity> boardList = boardRepository.findAll();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (BoardEntity board : boardList) {
            Long userId = board.getUser().getUserId();  // UserId는 BoardEntity의 User 객체에서 가져오기

            BoardDto boardDto = new BoardDto(
                    board.getProjectId(),
                    board.getUser().getUserId(),  // 수정된 부분: board.getUser().getUserId()로 UserId를 가져옴
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
                    board.getProjectName(),
                    board.getNickname(),
                    board.getViewCount()
            );
            log.info("Get board.getNickname : {}",board.getNickname());
            log.info("Board List에서의 조회수 : {}", board.getViewCount());
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
    public BoardDto getBoardById(Long projectId , HttpServletRequest request, HttpServletResponse response) {

        BoardEntity board = detail(projectId, request, response);

        // BoardEntity의 값을 확인하는 로그 추가
        log.info("BoardEntity projectName: {}, nickname: {}", board.getProjectName(), board.getNickname());

        return new BoardDto(
                board.getProjectId(),
                board.getUser().getUserId(),  // 수정된 부분: board.getUser().getUserId()로 UserId를 가져옴
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
                board.getProjectName(),
                board.getNickname(),
                board.getViewCount()
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