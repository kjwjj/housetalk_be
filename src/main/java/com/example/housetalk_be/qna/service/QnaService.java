package com.example.housetalk_be.qna.service;

import com.example.housetalk_be.qna.dto.QnaResponseDTO;
import com.example.housetalk_be.qna.entity.Qna;
import com.example.housetalk_be.qna.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public Qna submitQna(String userEmail, String title, String category, String content) {
        Qna qna = Qna.builder()
                .userEmail(userEmail)
                .title(title)
                .category(category) // 🔹 category 저장
                .content(content)
                .build();
        Qna saved = qnaRepository.save(qna);

        // 이메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("rhwjddn36@gmail.com");
        message.setSubject("[문의] " + title + " (" + category + ")");
        message.setText("문의자: " + userEmail + "\n\n내용:\n" + content);
        mailSender.send(message);

        return saved;
    }

    // 🔹 내 문의 내역 조회
    public List<Qna> getMyQna(String userEmail) {
        return qnaRepository.findAllByUserEmailOrderByQuestionDateDesc(userEmail);
    }


    // 🔹 관리자용 전체 문의 조회
    public List<Qna> getAllQna() {
        return qnaRepository.findAllByOrderByQuestionDateDesc();
    }
}