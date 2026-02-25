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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final JavaMailSender mailSender;
    private final OpenAiService openAiService;

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

    public Qna getQnaById(Long id) {
        return qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
    }

//    @Transactional
//    public Qna answerQna(Long id, String answer) {
//        Qna qna = qnaRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
//
//        qna.setAnswerContent(answer);
//        qna.setAnswerStatus("답변완료");
//        qna.setAnswerDate(LocalDateTime.now());
//
//        return qna;
//    }


// AI 초안 생성 (🔥 추가)
    public String generateAiDraft(Long id) {

        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));

        String prompt = """
            당신은 주거 플랫폼 고객센터 상담원입니다.

            문의 유형에 따라 답변 스타일을 다르게 작성하세요.

            - 일반: 기본 안내 및 서비스 설명을 친절하게 작성
            - 계정: 로그인, 비밀번호, 회원정보 관련 해결 방법을 구체적으로 안내
            - 오류: 발생 원인 추정 및 해결 방법을 단계별로 설명
            - 결제: 결제 상태, 환불 절차, 처리 기간을 명확히 안내

            문의 유형: %s
            제목: %s
            내용: %s

            답변을 작성하세요.
            ⚠️ 반드시 답변 마지막 줄에 아래 문구를 정확히 추가하세요:
                
             HouseTalk 고객센터 드림
        """.formatted(
                qna.getCategory(),
                qna.getTitle(),
                qna.getContent()
        );

        return openAiService.generateAnswer(prompt);
    }

    //  최종 답변 저장 (수정됨)

    @Transactional
    public Qna answerQna(Long id, String finalAnswer) {

        Qna qna = qnaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));

        qna.setAnswerContent(finalAnswer);
        qna.setAnswerStatus("답변완료");
        qna.setAnswerDate(LocalDateTime.now());


        // 🔹 답변 메일 발송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(qna.getUserEmail()); // 문의자 이메일
        message.setFrom("HouseTalk <rhwjddn36@gmail.com>"); // Gmail 계정은 그대로, 이름만 HouseTalk
        message.setSubject("[답변] " + qna.getTitle());
        message.setText(finalAnswer);
        mailSender.send(message);

        return qna;
    }
}