package com.example.housetalk_be.board.controller;

import com.example.housetalk_be.board.dto.CommentRequestDTO;
import com.example.housetalk_be.board.dto.CommentResponseDTO;
import com.example.housetalk_be.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards/{boardId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable Long boardId,
            @RequestBody CommentRequestDTO dto,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(commentService.createComment(boardId, dto, userEmail));
    }

    // 댓글 목록 조회
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @PathVariable Long boardId
    ) {
        return ResponseEntity.ok(commentService.getComments(boardId));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDTO dto,
            Authentication authentication
    ) {
        commentService.updateComment(commentId, dto.getContent(), authentication.getName());
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long boardId,
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        String email = authentication.getName();
        commentService.deleteComment(commentId, email);
        return ResponseEntity.ok().build();
    }
}