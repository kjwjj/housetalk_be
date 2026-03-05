package com.example.housetalk_be.board.dto;

import com.example.housetalk_be.board.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String username; // 이메일
    private String userName; // 실제 이름
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponseDTO> replies;

    public static CommentResponseDTO fromEntity(Comment comment) {
        Long userId = null;
        String userName = "탈퇴한 사용자";
        String username = "";

        if (comment.getUser() != null) {
            userId = comment.getUser().getId();
            userName = comment.getUser().getName();
            username = comment.getUser().getEmail();
        }

        return CommentResponseDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .username(username)
                .userName(userName)
                .userId(userId)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(comment.getReplies().stream()
                        .map(CommentResponseDTO::fromEntity)
                        .collect(Collectors.toList()))
                .build();
    }
}