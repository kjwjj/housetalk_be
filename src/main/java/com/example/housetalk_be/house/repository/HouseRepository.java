package com.example.housetalk_be.house.repository;

import com.example.housetalk_be.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HouseRepository extends JpaRepository<House, Long> {
    // 🔥 로그인한 사용자 이메일 기준으로 조회
    List<House> findByUser_Email(String email);

    List<House> findTop3ByOrderByViewCountDesc();
}
