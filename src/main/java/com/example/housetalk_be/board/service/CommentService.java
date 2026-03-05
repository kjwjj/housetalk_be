package com.example.housetalk_be.board.service;

import com.example.housetalk_be.board.dto.CommentRequestDTO;
import com.example.housetalk_be.board.dto.CommentResponseDTO;
import com.example.housetalk_be.board.entity.Board;
import com.example.housetalk_be.board.entity.Comment;
import com.example.housetalk_be.board.repository.BoardRepository;
import com.example.housetalk_be.board.repository.CommentRepository;
import com.example.housetalk_be.user.domain.User;
import com.example.housetalk_be.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserService userService;

    @Transactional
    public CommentResponseDTO createComment(Long boardId, CommentRequestDTO dto, String email) {
        // 1️⃣ 사용자 가져오기
        User user = userService.findByEmail(email);

        // 2️⃣ 게시글 가져오기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        // 3️⃣ 부모 댓글 확인 (대댓글이면 parentId 존재)
        Comment parent = null;
        if (dto.getParentId() != null) {
            parent = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 댓글 없음"));
        }

        // 4️⃣ 댓글 생성
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .user(user)
                .board(board)
                .parent(parent)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 5️⃣ 저장
        Comment saved = commentRepository.save(comment);

        // 6️⃣ DTO 반환 (userName 반드시 포함)
        return CommentResponseDTO.builder()
                .id(saved.getId())
                .content(saved.getContent())
                .userId(user.getId())
                .username(user.getEmail())
                .userName(user.getName()) // 🔥 여기가 핵심
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .replies(List.of()) // 새 댓글이니 아직 대댓글 없음
                .build();
    }

    // 🔥 fetch join 적용해서 댓글 + 대댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getComments(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        List<Comment> comments = commentRepository.findParentCommentsWithReplies(board);
        for (Comment c : comments) {
            System.out.println("parent id: " + c.getId() + ", user: " + (c.getUser() != null ? c.getUser().getName() : "null"));
            for (Comment r : c.getReplies()) {
                System.out.println("  reply id: " + r.getId() + ", user: " + (r.getUser() != null ? r.getUser().getName() : "null"));
            }
        }
        return comments.stream()
                .map(CommentResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<CommentResponseDTO> getComments(Long boardId, Pageable pageable) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        return commentRepository.findByBoardAndParentIsNull(board, pageable)
                .map(CommentResponseDTO::fromEntity);
    }

    @Transactional
    public void updateComment(Long commentId, String content, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        if (!comment.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("수정 권한 없음");
        }

        comment.updateContent(content);
    }

    @Transactional
    public void deleteComment(Long commentId, String email) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        User currentUser = userService.findByEmail(email);
        boolean isAdmin = currentUser.getRole().equals("ROLE_ADMIN");
        boolean isAuthor = comment.getUser().getId().equals(currentUser.getId());

        if (!(isAuthor || isAdmin)) {
            throw new IllegalArgumentException("삭제 권한 없음");
        }

        commentRepository.delete(comment);
    }
}