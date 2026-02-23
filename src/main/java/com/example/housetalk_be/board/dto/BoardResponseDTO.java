package com.example.housetalk_be.board.dto;

import com.example.housetalk_be.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BoardResponseDTO {

    private Long id;
    private String title;
    private String content;
    private String author;
    private Long authorId;   // 🔥 반드시 추가
    private Integer views;
    private LocalDateTime createdAt;

//    public static BoardResponseDTO fromEntity(Board board) {
//        return BoardResponseDTO.builder()
//                .id(board.getId())
//                .title(board.getTitle())
//                .content(board.getContent())
//                .author(board.getUser() != null ? board.getUser().getEmail() : "탈퇴한 사용자")
//                .authorId(board.getUser() != null ? board.getUser().getId() : null) // 🔥 이거 핵심
//                .views(board.getViews())
//                .createdAt(board.getCreatedAt())
//                .build();
//    }
public static BoardResponseDTO fromEntity(Board board) {
    Long authorId = null;
    String author = "탈퇴한 사용자";

    if (board.getUser() != null) {
        authorId = board.getUser().getId();
        author = board.getUser().getName();
    }

    return BoardResponseDTO.builder()
            .id(board.getId())
            .title(board.getTitle())
            .content(board.getContent())
            .author(author)
            .authorId(authorId)
            .views(board.getViews())
            .createdAt(board.getCreatedAt())
            .build();
}
}