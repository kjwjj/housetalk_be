package com.example.housetalk_be.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
