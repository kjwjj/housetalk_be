package com.example.housetalk_be.listings.entity;

import com.example.housetalk_be.house.entity.House;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 어떤 집에 속하는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TradeType tradeType;

    private Integer deposit;
    private Integer rent;
    private Integer salePrice;
    private Integer maintenanceFee;

    private LocalDate availableFrom;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ===== getter/setter =====

    public Long getId() { return id; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }

    public TradeType getTradeType() { return tradeType; }
    public void setTradeType(TradeType tradeType) { this.tradeType = tradeType; }

    public Integer getDeposit() { return deposit; }
    public void setDeposit(Integer deposit) { this.deposit = deposit; }

    public Integer getRent() { return rent; }
    public void setRent(Integer rent) { this.rent = rent; }

    public Integer getSalePrice() { return salePrice; }
    public void setSalePrice(Integer salePrice) { this.salePrice = salePrice; }

    public Integer getMaintenanceFee() { return maintenanceFee; }
    public void setMaintenanceFee(Integer maintenanceFee) { this.maintenanceFee = maintenanceFee; }

    public LocalDate getAvailableFrom() { return availableFrom; }
    public void setAvailableFrom(LocalDate availableFrom) { this.availableFrom = availableFrom; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}