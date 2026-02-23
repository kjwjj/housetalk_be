package com.example.housetalk_be.board.service;

import com.example.housetalk_be.board.dto.BoardRequestDTO;
import com.example.housetalk_be.board.dto.BoardResponseDTO;
import com.example.housetalk_be.board.entity.Board;
import com.example.housetalk_be.board.repository.BoardRepository;
import com.example.housetalk_be.user.domain.User;
import com.example.housetalk_be.user.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserService userService;

    // =========================
    // 게시글 등록
    // =========================
    @Transactional
    public Long create(BoardRequestDTO dto, String email) {

        User user = userService.findByEmail(email);

        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .views(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return boardRepository.save(board).getId();
    }

    // =========================
    // 목록 조회
    // =========================
    public Page<BoardResponseDTO> getBoards(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(BoardResponseDTO::fromEntity);
    }

    public Page<BoardResponseDTO> getBoardsByUser(String email, Pageable pageable) {
        User user = userService.findByEmail(email);
        return boardRepository.findByUser(user, pageable)
                .map(BoardResponseDTO::fromEntity);
    }

    // =========================
    // 상세 조회 + 조회수 증가
    // =========================
    @Transactional
    public BoardResponseDTO getBoard(Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        board.increaseViews();

        return BoardResponseDTO.fromEntity(board);
    }

    // =========================
    // 수정
    // =========================
    @Transactional
    public void update(Long id, BoardRequestDTO dto, String email) {

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        if (!board.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("수정 권한 없음");
        }

        board.update(dto.getTitle(), dto.getContent());
    }

    // =========================
    // 삭제
    // =========================
//    @Transactional
//    public void delete(Long id) {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        String email = auth.getName();
//        boolean isAdmin = auth.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
//
//        Board board = boardRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
//
//        boolean isAuthor = board.getUser().getEmail().equals(email);
//
//        if (!(isAuthor || isAdmin)) {
//            throw new IllegalArgumentException("삭제 권한 없음");
//        }
//
//        boardRepository.delete(board);
//    }
    @Transactional
    public void delete(Long id, Long userId, boolean isAdmin) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

//        boolean isAuthor = board.getUser().getId().equals(userId); // ⚡ userId 비교
        boolean isAuthor = board.getUser() != null && board.getUser().getId().equals(userId);
        if (!(isAuthor || isAdmin)) {
            throw new IllegalArgumentException("삭제 권한 없음");
        }

        boardRepository.delete(board);
    }
}