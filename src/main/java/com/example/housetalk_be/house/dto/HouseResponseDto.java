package com.example.housetalk_be.house.dto;


import com.example.housetalk_be.house.entity.House;
import com.example.housetalk_be.listings.dto.ListingResponseDto;

public record HouseResponseDto(
        Long id,
        String name,
        String address,
        String type,
        Integer rooms,
        String imagePath,
        Long userId,
        String userName,
        ListingResponseDto listing   // ✅ 추가
) {
    public static HouseResponseDto from(House house) {
        ListingResponseDto listingDto = null;

        if (house.getListings() != null && !house.getListings().isEmpty()) {
            listingDto = ListingResponseDto.from(
                    house.getListings().get(0)
            );
        }
        return new HouseResponseDto(
                house.getId(),
                house.getName(),
                house.getAddress(),
                house.getType(),
                house.getRooms(),
                house.getImagePath(),
                house.getUser() != null ? house.getUser().getId() : null,
                house.getUser() != null ? house.getUser().getName() : null,
                listingDto   // ✅ 여기
        );
    }
}