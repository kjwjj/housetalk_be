package com.example.housetalk_be.user.repository;

import com.example.housetalk_be.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.housetalk_be.user.domain.Role; //관리자 확인용

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    // 🔥 관리자 조회용 추가
    List<User> findAllByRole(Role role);
}