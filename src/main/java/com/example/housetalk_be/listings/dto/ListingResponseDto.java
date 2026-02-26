package com.example.housetalk_be.listings.dto;

import com.example.housetalk_be.listings.entity.Listing;

import java.time.LocalDate;

public record ListingResponseDto(
        Long id,
        Long houseId,
        String tradeType,
        Integer deposit,
        Integer rent,
        Integer salePrice,
        Integer maintenanceFee,
        LocalDate availableFrom
) {

    public static ListingResponseDto from(Listing listing) {
        return new ListingResponseDto(
                listing.getId(),
                listing.getHouse().getId(),
                listing.getTradeType().name(),
                listing.getDeposit(),
                listing.getRent(),
                listing.getSalePrice(),
                listing.getMaintenanceFee(),
                listing.getAvailableFrom()
        );
    }
}