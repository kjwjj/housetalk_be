package com.example.housetalk_be.house.repository;

import com.example.housetalk_be.house.entity.HouseLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface HouseLikeRepository extends JpaRepository<HouseLike, Long> {

    Optional<HouseLike> findByUser_IdAndHouse_Id(Long userId, Long houseId);

    int countByHouse_Id(Long houseId);

    List<HouseLike> findByUser_Email(String email);

    // User ID로 찜한 목록 조회
    List<HouseLike> findByUser_Id(Long userId);
}