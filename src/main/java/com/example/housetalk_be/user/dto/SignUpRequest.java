package com.example.housetalk_be.user.dto;


import lombok.Getter;
import java.time.LocalDate;

@Getter
public class SignUpRequest {
    private String email;
    private String password;
    private String name;
    private LocalDate birth;
    private String gender;
    private String phone;
}