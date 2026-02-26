package com.example.housetalk_be.listings.controller;

import com.example.housetalk_be.house.entity.House;
import com.example.housetalk_be.house.service.HouseService;
import com.example.housetalk_be.listings.dto.ListingRequestDto;
import com.example.housetalk_be.listings.dto.ListingResponseDto;
import com.example.housetalk_be.listings.entity.Listing;
import com.example.housetalk_be.listings.entity.TradeType;
import com.example.housetalk_be.listings.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
@CrossOrigin(origins = "http://localhost:5173")
public class ListingController {

    private final ListingService listingService;
    private final HouseService houseService;

    public ListingController(ListingService listingService,
                             HouseService houseService) {
        this.listingService = listingService;
        this.houseService = houseService;
    }

    @PostMapping
    public ResponseEntity<ListingResponseDto> addListing(
            @RequestBody ListingRequestDto dto
    ) {

        House house = houseService.findById(dto.getHouseId())
                .orElseThrow(() -> new RuntimeException("집이 존재하지 않습니다."));

        Listing listing = new Listing();
        listing.setHouse(house);
        listing.setTradeType(TradeType.valueOf(dto.getTradeType().toUpperCase())); // 중요
        listing.setDeposit(dto.getDeposit());
        listing.setRent(dto.getRent());
        listing.setSalePrice(dto.getSalePrice());
        listing.setMaintenanceFee(dto.getMaintenanceFee());
        listing.setAvailableFrom(dto.getAvailableFrom());

        Listing saved = listingService.save(listing);

        return ResponseEntity.ok(ListingResponseDto.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<ListingResponseDto>> getAll() {
        return ResponseEntity.ok(
                listingService.findAll()
                        .stream()
                        .map(ListingResponseDto::from)
                        .toList()
        );
    }
}