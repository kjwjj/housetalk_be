package com.example.housetalk_be.house.entity;

import com.example.housetalk_be.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.example.housetalk_be.listings.entity.Listing;
import java.util.List;
@Entity
@Table(name = "house")
public class House {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String type;
    private Integer rooms;
    private String imagePath;

    @Column(columnDefinition = "TEXT")  // 🔥 긴 글 저장 가능
    private String context;

    @Column(nullable = false)
    private int viewCount = 0;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Listing> listings;

    public List<Listing> getListings() {
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }


    // 🔑 작성자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getRooms() { return rooms; }
    public void setRooms(Integer rooms) { this.rooms = rooms; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public int getViewCount() {return viewCount;}
    public void setViewCount(int viewCount) {this.viewCount = viewCount;}

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

}