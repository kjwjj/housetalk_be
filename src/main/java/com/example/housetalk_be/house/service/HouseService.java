package com.example.housetalk_be.house.service;

import com.example.housetalk_be.house.entity.House;
import com.example.housetalk_be.house.repository.HouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HouseService {

    private final HouseRepository houseRepository;

    public HouseService(HouseRepository houseRepository) {

        this.houseRepository = houseRepository;
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

    // 🔹 총 매물 수 반환
    public long countHouses() {
        return houseRepository.count();
    }
}