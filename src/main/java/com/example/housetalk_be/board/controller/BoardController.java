package com.example.housetalk_be.board.controller;


import com.example.housetalk_be.board.dto.BoardRequestDTO;
import com.example.housetalk_be.board.dto.BoardResponseDTO;
import com.example.housetalk_be.board.service.BoardService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 등록
    @PostMapping
    public ResponseEntity<Long> create(
            @RequestBody BoardRequestDTO dto,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(boardService.create(dto, username));
    }

    // 목록
    @GetMapping
    public ResponseEntity<Page<BoardResponseDTO>> getBoards(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(boardService.getBoards(pageable));
    }

    // 상세
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDTO> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoard(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody BoardRequestDTO dto,
            Authentication authentication
    ) {
        boardService.update(id, dto, authentication.getName());
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        boardService.delete(id, username, role);
        return ResponseEntity.ok().build();
    }

    // 🔹 내 게시글만 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyBoards(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        Page<BoardResponseDTO> boards = boardService.getBoardsByUser(authentication.getName(), Pageable.ofSize(10));
        return ResponseEntity.ok(boards);
    }

}