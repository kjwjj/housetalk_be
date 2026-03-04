package com.example.housetalk_be.house.service;

import com.example.housetalk_be.house.entity.House;
import com.example.housetalk_be.house.entity.HouseLike;
import com.example.housetalk_be.house.repository.HouseLikeRepository;
import com.example.housetalk_be.house.repository.HouseRepository;
import com.example.housetalk_be.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HouseService {

    private final HouseLikeRepository houseLikeRepository;
    private final HouseRepository houseRepository;

    // ✅ 생성자 수정 (중요)
    public HouseService(HouseRepository houseRepository,
                        HouseLikeRepository houseLikeRepository) {
        this.houseRepository = houseRepository;
        this.houseLikeRepository = houseLikeRepository;
    }


    public House save(House house) {

        return houseRepository.save(house);
    }

    public List<House> findAll() {

        return houseRepository.findAll();
    }

    public Optional<House> findById(Long id) {

        return houseRepository.findById(id);
    }

    public void delete(House house) {

        houseRepository.delete(house);
    }

    @Transactional
    public void increaseView(Long houseId) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new RuntimeException("House not found"));

        house.setViewCount(house.getViewCount() + 1);
    }

    public List<House> findByUserEmail(String email) {
        return houseRepository.findByUser_Email(email);
    }

    public List<House> findPopularHouses() {
        return houseRepository.findTop3ByOrderByViewCountDesc();
    }


    // ✅ ✅ 좋아요 토글 기능
    @Transactional
    public void toggleLike(Long houseId, User user) {

        Optional<HouseLike> existing =
                houseLikeRepository.findByUser_IdAndHouse_Id(user.getId(), houseId);

        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> new RuntimeException("매물 없음"));

        if (existing.isPresent()) {
            houseLikeRepository.delete(existing.get()); // 좋아요 취소
        } else {
            HouseLike like = new HouseLike();
            like.setUser(user);
            like.setHouse(house);
            houseLikeRepository.save(like); // 좋아요 등록
        }
    }

    // ✅ 좋아요 개수 조회 (추가)
    public int getLikeCount(Long houseId) {
        return houseLikeRepository.countByHouse_Id(houseId);
    }

    // 내가 좋아요 눌렀는지 여부
    public boolean isLikedByUser(Long houseId, Long userId) {
        return houseLikeRepository
                .findByUser_IdAndHouse_Id(userId, houseId)
                .isPresent();
    }

    // =========================
    // 🔥 내가 찜한 매물 조회 (추가된 부분)
    // =========================
    public List<HouseLike> findLikedByUser(Long userId) {
        return houseLikeRepository.findByUser_Id(userId);
    }



    // 🔹 총 매물 수 반환
    public long countHouses() {
        return houseRepository.count();
    }
}