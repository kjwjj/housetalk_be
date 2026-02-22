package com.example.housetalk_be.house.dto;


import com.example.housetalk_be.house.entity.House;

public record HouseResponseDto(
        Long id,
        String name,
        String address,
        Integer price,
        String type,
        Integer rooms,
        String imagePath,
        Long userId,
        String userName
) {
    public static HouseResponseDto from(House house) {
        return new HouseResponseDto(
                house.getId(),
                house.getName(),
                house.getAddress(),
                house.getPrice(),
                house.getType(),
                house.getRooms(),
                house.getImagePath(),
                house.getUser() != null ? house.getUser().getId() : null,
                house.getUser() != null ? house.getUser().getName() : null
        );
    }
}