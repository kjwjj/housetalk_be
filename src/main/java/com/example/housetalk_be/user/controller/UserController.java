package com.example.housetalk_be.user.controller;

import com.example.housetalk_be.auth.jwt.JwtUtil;
import com.example.housetalk_be.user.domain.User;
import com.example.housetalk_be.user.dto.PasswordChangeRequest;
import com.example.housetalk_be.user.dto.SignUpRequest;
import com.example.housetalk_be.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173") // 프론트 주소 허용
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // -------------------
    // 회원가입
    // -------------------
    @PostMapping("/signup/form")
    public ResponseEntity<Map<String, String>> signup(@RequestBody SignUpRequest request) {
        try {
            userService.signup(request);

            Map<String, String> result = new HashMap<>();
            result.put("message", "회원가입 성공");
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // -------------------
    // 로그인
    // -------------------
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            // 🔑 로그인 인증
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

        } catch (BadCredentialsException e) {
            // 비밀번호 틀린 경우
            Map<String, String> error = new HashMap<>();
            error.put("message", "이메일 또는 비밀번호가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (AuthenticationException e) {
            // 그 외 인증 실패
            Map<String, String> error = new HashMap<>();
            error.put("message", "로그인 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        // 🔑 JWT 생성
        String token = jwtUtil.generateToken(email);

        // 🔑 여기서 이메일로 유저 이름 조회
        User user = userService.findByEmail(email);

        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        result.put("message", "로그인 성공");
        result.put("userName", user.getName()); // ✅ 이름 추가
        result.put("userId", String.valueOf(user.getId()));
        result.put("role", user.getRole().name());  // ✅ 이거 추가
        return ResponseEntity.ok(result);
    }

    // -------------------
    // 비밀번호 찾기
    // -------------------
    @PostMapping("/find-password")
    public ResponseEntity<Map<String, String>> findPassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        Map<String, String> result = new HashMap<>();

        try {
            userService.resetPassword(email);
            result.put("message", "임시 비밀번호를 이메일로 발송했습니다.");
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    // -------------------
    // 마이페이지 비밀번호 재확인
    // -------------------
    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, String>> verifyPassword(
            @RequestBody Map<String, String> request) {

        String password = request.get("password");

        // 🔥 JWT에서 현재 로그인한 사용자 이메일 꺼내기
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userService.findByEmail(email);

        Map<String, String> result = new HashMap<>();

        if (!userService.matchesPassword(password, user.getPassword())) {
            result.put("message", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        result.put("message", "비밀번호 확인 완료");
        return ResponseEntity.ok(result);
    }

    // -------------------
    // 내 정보 조회
    // -------------------
    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromToken(token);

        User user = userService.findByEmail(email);
        user.setPassword(null); // 비밀번호는 절대 보내지 않음

        return ResponseEntity.ok(user);
    }


    // -------------------
    // 내 정보 수정
    // -------------------
    @PutMapping("/me")
    public ResponseEntity<Map<String, String>> updateMyInfo(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request) {

        String token = authHeader.substring(7);
        String email = jwtUtil.getEmailFromToken(token);

        userService.updateUserInfo(email, request);

        Map<String, String> result = new HashMap<>();
        result.put("message", "정보 수정 완료");
        return ResponseEntity.ok(result);
    }

    // -------------------
    // 회원 탈퇴
    // -------------------
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteUser(userDetails.getUsername());
        return ResponseEntity.ok("회원 탈퇴 완료");
    }

//    // -------------------
//    // 비밀번호 변경
//    // -------------------
//    @PutMapping("/change-password")
//    public ResponseEntity<Map<String, String>> changePassword(
//            @RequestHeader("Authorization") String authHeader,
//            @Valid @RequestBody PasswordChangeRequest request) {
//
//        String token = authHeader.substring(7);
//        String email = jwtUtil.getEmailFromToken(token);
//
//        userService.changePassword(
//                email,
//                request.getCurrentPassword(),
//                request.getNewPassword(),
//                request.getConfirmPassword()
//        );
//
//        Map<String, String> result = new HashMap<>();
//        result.put("message", "비밀번호 변경 완료");
//        return ResponseEntity.ok(result);
//    }
// -------------------
// 비밀번호 변경
// -------------------
@PutMapping("/change-password")
public ResponseEntity<Map<String, String>> changePassword(
        @RequestHeader("Authorization") String authHeader,
        @Valid @RequestBody PasswordChangeRequest request) {

    String token = authHeader.substring(7);
    String email = jwtUtil.getEmailFromToken(token);

    // 비밀번호 변경
    userService.changePassword(
            email,
            request.getCurrentPassword(),
            request.getNewPassword(),
            request.getConfirmPassword()
    );

    // 🔑 변경 후 새 JWT 발급
    String newToken = jwtUtil.generateToken(email);

    Map<String, String> result = new HashMap<>();
    result.put("message", "비밀번호 변경 완료");
    result.put("token", newToken); // 새 토큰 반환
    return ResponseEntity.ok(result);
}
}
