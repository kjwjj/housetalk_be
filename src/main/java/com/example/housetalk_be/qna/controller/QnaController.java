package com.example.housetalk_be.qna.controller;

import com.example.housetalk_be.qna.dto.QnaResponseDTO;
import com.example.housetalk_be.qna.entity.Qna;
import com.example.housetalk_be.qna.service.QnaService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/qna")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // React 프론트와 연결
public class QnaController {

    private final QnaService qnaService;

    // 🔹 문의 제출
    @PostMapping
    public Qna submitQna(@AuthenticationPrincipal UserDetails user, @RequestBody QnaRequest request) {
        String email = user.getUsername();
        return qnaService.submitQna(
                email,
                request.getTitle(),
                request.getCategory(),
                request.getContent());
    }

    // 🔹 내 문의 내역 조회
    @GetMapping("/mine")
    public List<Qna> getMyQna(@AuthenticationPrincipal UserDetails user) {
        String email = user.getUsername();
        return qnaService.getMyQna(email);
    }



    // 관리자용: 전체 문의 조회
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')") // 관리자만 접근 가능
    public List<Qna> getAllQna() {
        return qnaService.getAllQna();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Qna getQnaDetail(@PathVariable Long id) {
        return qnaService.getQnaById(id);
    }

    // 🔥 AI 초안 생성 API (추가된 부분)
    @GetMapping("/{id}/draft")
    @PreAuthorize("hasRole('ADMIN')")
    public String generateDraft(@PathVariable Long id) {
        return qnaService.generateAiDraft(id);
    }

    // 🔹 최종 답변 저장
    @PutMapping("/{id}/answer")
    @PreAuthorize("hasRole('ADMIN')")
    public Qna answerQna(@PathVariable Long id,
                         @RequestBody AnswerRequest request) {

        return qnaService.answerQna(id, request.getAnswer());
    }
//
//    @PutMapping("/{id}/answer")
//    @PreAuthorize("hasRole('ADMIN')")
//    public Qna answerQna(@PathVariable Long id, @RequestBody AnswerRequest request) {
//        return qnaService.answerQna(id, request.getAnswer());
//    }

    @Getter
    @Setter
    public static class AnswerRequest {
        private String answer;
    }

    @Getter
    @Setter
    public static class QnaRequest {
        private String userEmail;
        private String title;
        private String category;
        private String content;
    }
}