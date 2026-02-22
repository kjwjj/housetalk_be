package com.example.housetalk_be.auth.controller;

import com.example.housetalk_be.auth.dto.EmailRequest;
import com.example.housetalk_be.auth.dto.EmailVerifyRequest;
import com.example.housetalk_be.auth.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestBody EmailRequest request) {
        emailAuthService.sendCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@RequestBody EmailVerifyRequest request) {
        emailAuthService.verify(request.getEmail(), request.getCode());
        return ResponseEntity.ok().build();
    }
}