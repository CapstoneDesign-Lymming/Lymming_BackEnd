package com.supernova.lymming.heart.service;

import com.supernova.lymming.board.dto.BoardDto;
import com.supernova.lymming.board.entity.BoardEntity;
import com.supernova.lymming.board.repository.BoardRepository;
import com.supernova.lymming.github.entity.User;
import com.supernova.lymming.github.repository.UserRepository;
import com.supernova.lymming.heart.entity.HeartEntity;
import com.supernova.lymming.heart.repository.HeartRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class HeartService {
    private final HeartRepository heartRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public HeartService(HeartRepository heartRepository, UserRepository userRepository, BoardRepository boardRepository) {
        this.heartRepository = heartRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    public void likeProject(Long userId, Long projectId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
        BoardEntity project = boardRepository.findByProjectId(projectId).orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다."));

        if(heartRepository.existsByUserIdAndProjectId(user, project)){
            throw new RuntimeException("이미 좋아요 등록한 게시물 입니다.");
        }

        HeartEntity heart = new HeartEntity();
        heart.setUserId(user);
        heart.setProjectId(project);
        heartRepository.save(heart);

        // BoardDto 생성 후 좋아요 상태 설정
        BoardDto boardDto = new BoardDto();
        boardDto.setProjectId(project.getProjectId());

        // 좋아요가 눌린 프로젝트라면 like를 true로 설정
        // heartRepository에서 해당 프로젝트에 좋아요가 눌렸는지 확인 후 처리
        boolean like = heartRepository.existsByUserIdAndProjectId(user, project);
        boardDto.setLike(like);  // like가 true이면 좋아요 누른 상태

        log.info("Project ID: {}, like: {}", boardDto.getProjectId(), boardDto.isLike());
    }

    public void unlikeProject(Long userId, Long projectId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. 로그인 해주세요"));
        BoardEntity project = boardRepository.findByProjectId(projectId).orElseThrow(() -> new RuntimeException("게시물을 찾을 수 없습니다."));

        HeartEntity heart = heartRepository.findByUserIdAndProjectId(user,project)
                .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));

        heartRepository.delete(heart);
    }

    public List<BoardEntity> getLikeProjects(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return heartRepository.findAllByUserId(user).stream()
                .map(HeartEntity::getProjectId)
                .collect(Collectors.toList());
    }
}
