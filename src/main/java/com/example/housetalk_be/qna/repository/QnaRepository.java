package com.example.housetalk_be.qna.repository;

import com.example.housetalk_be.qna.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
    // 로그인한 이메일로 문의 내역 조회
    List<Qna> findAllByUserEmailOrderByQuestionDateDesc(String userEmail);

    List<Qna> findAllByOrderByQuestionDateDesc(); // 전체 문의 조회
}