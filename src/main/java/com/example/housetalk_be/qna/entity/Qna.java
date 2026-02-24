package com.example.housetalk_be.qna.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "qna")
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    private String title;

    @Column(length = 50)
    private String category; // 🔹 추가


    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime questionDate;

    private String answerStatus; // 대기중, 답변완료

    @Column(columnDefinition = "TEXT")
    private String answerContent;

    private LocalDateTime answerDate;

    @PrePersist
    public void prePersist() {
        questionDate = LocalDateTime.now();
        if (answerStatus == null) answerStatus = "대기중";
    }
}