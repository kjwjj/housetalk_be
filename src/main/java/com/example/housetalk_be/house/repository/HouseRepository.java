package com.example.housetalk_be.house.repository;

import com.example.housetalk_be.house.entity.House;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, Long> {
}
