package com.supernova.lymming.board.service;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.heart.repository.HeartRepository;
import com.supernova.lymming.sharepage.entity.SharePageEntity;
import com.supernova.lymming.sharepage.repository.SharePageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository; // UserRepository 추가
    private final SharePageRepository sharePageRepository; // SharePageRepository 추가
    private final HeartRepository heartRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository, UserRepository userRepository, SharePageRepository sharePageRepository,
                        HeartRepository heartRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.sharePageRepository = sharePageRepository;
        this.heartRepository = heartRepository;
    }

    public BoardDto createBoard(BoardDto boardDto) {
        // 새로운 게시판 생성
        BoardEntity board = new BoardEntity();
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

        // nickname을 BoardEntity에 설정
        board.setNickname(user.getNickname());

        // 게시판 저장
        boardRepository.save(board);

        // SharePageEntity 생성 및 저장
        SharePageEntity sharePage = new SharePageEntity();
        sharePage.setUser(user);  // User 객체 설정
        sharePage.setBoard(board);  // 생성된 BoardEntity와 연결
        sharePage.setLeader(board.getUser().getNickname());
        sharePage.setSharePageUrl(board.getProjectImg());
        sharePage.setMemberUrlBundle(user.getUserImg());
        sharePage.setPositionBundle(user.getPosition());
        sharePage.setTeamMember(user.getNickname());

        sharePageRepository.save(sharePage);

        return boardDto;
    }

    @Transactional
    public List<BoardDto> getBoardList() {
        List<BoardEntity> boardList = boardRepository.findAll();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (BoardEntity board : boardList) {
            Long userId = board.getUser().getUserId();  // UserId는 BoardEntity의 User 객체에서 가져오기

            // BoardDto 빌더를 사용하여 isHearted를 제외한 필드만 설정
            BoardDto boardDto = BoardDto.builder()
                    .projectId(board.getProjectId())
                    .userId(board.getUser().getUserId())  // UserId를 board.getUser().getUserId()로 가져옴
                    .studyType(board.getStudyType())
                    .uploadTime(board.getUploadTime())
                    .recruitmentField(board.getRecruitmentField())
                    .description(board.getDescription())
                    .userImg(board.getUser().getUserImg())
                    .workType(board.getWorkType())
                    .techStack(board.getTechStack())
                    .deadline(board.getDeadline())
                    .projectImg(board.getProjectImg())
                    .recruitmentCount(board.getRecruitmentCount())
                    .studyMethod(board.getStudyMethod())
                    .projectDuration(board.getProjectDuration())
                    .projectName(board.getProjectName())
                    .nickname(board.getNickname())
                    .viewCount(board.getViewCount()) // isHearted는 제외
                    .build();

            boardDtoList.add(boardDto);
        }

        return boardDtoList;
    }


    public BoardDto update(Long projectId, BoardDto boardDto) {
        BoardEntity board = boardRepository.findByProjectId(projectId)
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

        return BoardDto.builder()
                .projectId(board.getProjectId())
                .userId(board.getUser().getUserId())  // UserId를 board.getUser().getUserId()로 가져옴
                .studyType(board.getStudyType())
                .uploadTime(board.getUploadTime())
                .recruitmentField(board.getRecruitmentField())
                .description(board.getDescription())
                .userImg(board.getUser().getUserImg())
                .workType(board.getWorkType())
                .techStack(board.getTechStack())
                .deadline(board.getDeadline())
                .projectImg(board.getProjectImg())
                .recruitmentCount(board.getRecruitmentCount())
                .studyMethod(board.getStudyMethod())
                .projectDuration(board.getProjectDuration())
                .projectName(board.getProjectName())
                .nickname(board.getNickname())
                .viewCount(board.getViewCount())
                .build();  // isHearted 필드는 제외
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

    @Transactional
    public List<BoardDto> getBoardsWithHearts(Long userId) {
        log.info("getBoardsWithHearts 메소드 들어옴");

        // 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 모든 게시글 조회
        List<BoardEntity> boardList = boardRepository.findAll();
        List<BoardDto> boardDtoList = new ArrayList<>();

        for (BoardEntity board : boardList) {
            // 해당 게시글을 사용자가 찜했는지 확인
            boolean isHearted = heartRepository.existsByUserIdAndProjectId(user, board);

            // BoardDto 생성
            BoardDto boardDto = new BoardDto(
                    board.getProjectId(),
                    board.getUser().getUserId(),
                    board.getStudyType(),
                    board.getUploadTime(),
                    board.getRecruitmentField(),
                    board.getDescription(),
                    board.getUser().getUserImg(),
                    board.getWorkType(),
                    board.getTechStack(),
                    board.getDeadline(),
                    board.getProjectImg(),
                    board.getRecruitmentCount(),
                    board.getStudyMethod(),
                    board.getProjectDuration(),
                    board.getProjectName(),
                    board.getNickname(),
                    board.getViewCount(),
                    isHearted // 찜 여부 추가
            );

            boardDtoList.add(boardDto);
        }

        return boardDtoList;
    }

}