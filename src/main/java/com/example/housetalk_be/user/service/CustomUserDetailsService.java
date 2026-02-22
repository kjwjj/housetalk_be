package com.example.housetalk_be.user.service;

import com.example.housetalk_be.user.domain.User;
import com.example.housetalk_be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Optional;


// Spring Security 로그인용 인증 브릿지
// 로그인 시 사용자 정보를 가져와 인증
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

    // ✅ 여기 추가: DB에서 User 엔티티 조회
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}