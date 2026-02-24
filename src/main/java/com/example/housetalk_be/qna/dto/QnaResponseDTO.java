package com.example.housetalk_be.qna.dto;

import com.example.housetalk_be.qna.entity.Qna;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class QnaResponseDTO {
    private Long id;
    private String userEmail;
    private String title;
    private String category;
    private String content;
    private String answerStatus;
    private LocalDateTime questionDate;

    public static QnaResponseDTO fromEntity(Qna qna) {
        return new QnaResponseDTO(
                qna.getId(),
                qna.getUserEmail(),
                qna.getTitle(),
                qna.getCategory(),
                qna.getContent(),
                qna.getAnswerStatus(),
                qna.getQuestionDate()
        );
    }
}