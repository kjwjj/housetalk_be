package com.example.housetalk_be.user.service;

import com.example.housetalk_be.auth.service.EmailAuthService;
import com.example.housetalk_be.user.domain.Role;
import com.example.housetalk_be.user.domain.User;
import com.example.housetalk_be.user.dto.SignUpRequest;
import com.example.housetalk_be.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

import java.util.UUID;

// 회원 관련 비즈니스 로직 처리
// 회원 가입/ 유저 조회/ 비밀번호 찾기 등 실제 서비스 기능 수행
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final EmailAuthService emailAuthService;
    private final PasswordEncoder passwordEncoder;



    // 회원가입
    public void signup(SignUpRequest request) {

        if (!emailAuthService.isVerified(request.getEmail()))
            throw new IllegalStateException("이메일 인증이 필요합니다.");

        if (userRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .birth(request.getBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .role(Role.ROLE_USER) // ✅ 여기서 기본값 지정 집에서 확인해 볼것
                .build();

        userRepository.save(user);
    }

    // 🔑 이메일로 유저 조회
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }


    // 🔑 비밀번호 찾기 (임시 비밀번호 발급)
    public void resetPassword(String email) {

        User user = findByEmail(email);

        // 임시 비밀번호 생성
        String tempPassword = UUID.randomUUID().toString()
                .replace("-", "")
                .substring(0, 10);

        // 암호화 후 저장
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // 🔥 EmailAuthService로 HTML 메일 발송
        emailAuthService.sendTempPassword(user.getEmail(), tempPassword);
    }

    // 비밀번호 확인
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 내정보 수정
    public void updateUserInfo(String email, Map<String, String> request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setName(request.get("name"));
        user.setPhone(request.get("phone"));

        userRepository.save(user);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        userRepository.delete(user);
    }

    // 비밀번호 변경
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changePassword(String email,
                               String currentPassword,
                               String newPassword,
                               String confirmPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        userRepository.flush();

        // 디버깅용
//        System.out.println("DB 저장 비밀번호: " + user.getPassword());
//        System.out.println("matches 새비밀번호: " + passwordEncoder.matches(newPassword, user.getPassword()));
    }
}