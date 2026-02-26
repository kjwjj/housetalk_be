package com.example.housetalk_be.listings.service;


import com.example.housetalk_be.listings.entity.Listing;
import com.example.housetalk_be.listings.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public Listing save(Listing listing) {
        return listingRepository.save(listing);
    }

    public Optional<Listing> findById(Long id) {
        return listingRepository.findById(id);
    }

    public List<Listing> findByHouseId(Long houseId) {
        return listingRepository.findByHouseId(houseId);
    }

    public List<Listing> findAll() {
        return listingRepository.findAll();
    }

    public void delete(Listing listing) {
        listingRepository.delete(listing);
    }
}