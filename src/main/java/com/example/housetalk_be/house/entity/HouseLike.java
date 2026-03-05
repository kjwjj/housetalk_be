package com.example.housetalk_be.house.entity;

import com.example.housetalk_be.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "house_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "house_id"}))
public class HouseLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    private LocalDateTime createdAt = LocalDateTime.now();

    // getter setter
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public House getHouse() { return house; }
    public void setHouse(House house) { this.house = house; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}