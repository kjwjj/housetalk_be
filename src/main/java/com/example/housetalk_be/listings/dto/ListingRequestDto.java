package com.example.housetalk_be.listings.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ListingRequestDto {

    private Long houseId;
    private String tradeType;
    private Integer deposit;
    private Integer rent;
    private Integer salePrice;
    private Integer maintenanceFee;
    private LocalDate availableFrom;
}