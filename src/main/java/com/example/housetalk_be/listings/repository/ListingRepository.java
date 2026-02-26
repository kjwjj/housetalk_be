package com.example.housetalk_be.listings.repository;

import com.example.housetalk_be.listings.entity.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByHouseId(Long houseId);

}