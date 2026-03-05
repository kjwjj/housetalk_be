package com.example.housetalk_be.house.controller;

import com.example.housetalk_be.house.dto.HouseResponseDto;
import com.example.housetalk_be.house.entity.House;
import com.example.housetalk_be.house.entity.HouseLike;
import com.example.housetalk_be.house.service.HouseService;
import com.example.housetalk_be.user.domain.User;
import com.example.housetalk_be.user.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.housetalk_be.user.domain.Role;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/houses")
@CrossOrigin(origins = "http://localhost:5173")
public class HouseController {

    private final HouseService houseService;
    private final CustomUserDetailsService userDetailsService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public HouseController(HouseService houseService,
                           CustomUserDetailsService userDetailsService) {
        this.houseService = houseService;
        this.userDetailsService = userDetailsService;
    }

    // =========================
    // 매물 등록
    // =========================
    @PostMapping
    public ResponseEntity<HouseResponseDto> addHouse(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer rooms,
            @RequestParam(required = false) String context,   // ✅ 추가
            @RequestParam(required = false) MultipartFile[] images
    ) throws IOException {

        User user = userDetailsService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("로그인 사용자 없음"));

        House house = new House();
        house.setName(name);
        house.setAddress(address);
        house.setType(type);
        house.setRooms(rooms != null ? rooms : 0);
        house.setUser(user);
        house.setContext(context);

        if (images != null && images.length > 0) {
            StringBuilder sb = new StringBuilder();
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            for (MultipartFile file : images) {
                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                File dest = new File(folder, filename);
                file.transferTo(dest);
                sb.append(filename).append(",");
            }
            house.setImagePath(sb.toString());
        }

        House saved = houseService.save(house);
        return ResponseEntity.ok(HouseResponseDto.from(saved,0,false));
    }

    // =========================
    // 수정
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<HouseResponseDto> updateHouse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam(required = false) String context   // ✅ 추가
    ) {
        House house = houseService.findById(id)
                .orElseThrow(() -> new RuntimeException("매물이 존재하지 않습니다."));

        if (!house.getUser().getEmail().equals(principal.getUsername())) {
            return ResponseEntity.status(403).build();
        }

        house.setName(name);
        house.setAddress(address);
        house.setContext(context);

        House updated = houseService.save(house);

        int likeCount = houseService.getLikeCount(updated.getId());
        boolean likedByMe = houseService.isLikedByUser(
                updated.getId(),
                house.getUser().getId()
        );

        return ResponseEntity.ok(HouseResponseDto.from(updated, likeCount, likedByMe));
    }

    // =========================
    // 삭제
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHouse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal
    ) {
        House house = houseService.findById(id)
                .orElseThrow(() -> new RuntimeException("매물이 존재하지 않습니다."));

        User loginUser = userDetailsService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("로그인 사용자 없음"));

        // User가 null일 수도 있음 → null 체크 추가
        boolean isOwner = house.getUser() != null && house.getUser().getEmail().equals(principal.getUsername());
        boolean isAdmin = loginUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(403).build();
        }

        houseService.delete(house);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // 조회수 증가
    // =========================
    @PatchMapping("/{id}/view")
    public ResponseEntity<?> increaseView(@PathVariable Long id) {
        houseService.increaseView(id);
        return ResponseEntity.ok().build();
    }

    // =========================
    // 인기 매물
    // =========================
    @GetMapping("/popular")
    public ResponseEntity<List<HouseResponseDto>> getPopularHouses(
            @AuthenticationPrincipal UserDetails principal
    ) {

        User loginUser = (principal != null)
                ? userDetailsService.findByEmail(principal.getUsername()).orElse(null)
                : null;

        List<House> houses = houseService.findPopularHouses();

        List<HouseResponseDto> dtos = houses.stream()
                .map(house -> {

                    int likeCount = houseService.getLikeCount(house.getId());

                    boolean likedByMe = false;
                    if (loginUser != null) {
                        likedByMe = houseService
                                .isLikedByUser(house.getId(), loginUser.getId());
                    }

                    return HouseResponseDto.from(house, likeCount, likedByMe);
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }


    // =========================
    // 좋아요 토글
    // =========================
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal
    ) {
        User user = userDetailsService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("로그인 사용자 없음"));

        houseService.toggleLike(id, user);
        return ResponseEntity.ok().build();
    }

    // =========================
    // 전체 목록
    // =========================
    @GetMapping
    public ResponseEntity<List<HouseResponseDto>> getHouses(
            @AuthenticationPrincipal UserDetails principal
    ) {

        User loginUser = (principal != null)
                ? userDetailsService.findByEmail(principal.getUsername()).orElse(null)
                : null;

        List<House> houses = houseService.findAll();

        List<HouseResponseDto> dtos = houses.stream()
                .map(house -> {

                    int likeCount = houseService.getLikeCount(house.getId());

                    boolean likedByMe = false;
                    if (loginUser != null) {
                        likedByMe = houseService
                                .isLikedByUser(house.getId(), loginUser.getId());
                    }

                    return HouseResponseDto.from(house, likeCount, likedByMe);
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // =========================
    // 내 매물 목록
    // =========================
    @GetMapping("/my")
    public ResponseEntity<List<HouseResponseDto>> getMyHouses(
            @AuthenticationPrincipal UserDetails principal
    ) {

        User loginUser = userDetailsService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("로그인 사용자 없음"));

        List<House> houses =
                houseService.findByUserEmail(principal.getUsername());

        List<HouseResponseDto> dtos = houses.stream()
                .map(house -> {

                    int likeCount = houseService.getLikeCount(house.getId());

                    boolean likedByMe =
                            houseService.isLikedByUser(house.getId(), loginUser.getId());

                    return HouseResponseDto.from(house, likeCount, likedByMe);
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }


    // =========================
// 🔥 내가 찜한 매물 목록
// =========================
    @GetMapping("/liked")
    public ResponseEntity<List<HouseResponseDto>> getLikedHouses(
            @AuthenticationPrincipal UserDetails principal
    ) {

        User loginUser = userDetailsService.findByEmail(principal.getUsername())
                .orElseThrow(() -> new RuntimeException("로그인 사용자 없음"));

        // houseService에서 찜한 매물 가져오기
        List<HouseLike> likedList = houseService.findLikedByUser(loginUser.getId());

        List<HouseResponseDto> dtos = likedList.stream()
                .map(like -> {
                    House house = like.getHouse();
                    int likeCount = houseService.getLikeCount(house.getId());
                    boolean likedByMe = true; // 찜한 거니까 항상 true
                    return HouseResponseDto.from(house, likeCount, likedByMe);
                })
                .toList();

        return ResponseEntity.ok(dtos);
    }

    // 총 매물수 조회
    @GetMapping("/count")
    public ResponseEntity<Long> getHouseCount() {
        long count = houseService.countHouses();
        return ResponseEntity.ok(count);
    }
}