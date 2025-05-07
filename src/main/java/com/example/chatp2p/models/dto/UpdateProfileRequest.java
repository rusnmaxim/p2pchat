package com.example.chatp2p.models.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateProfileRequest {
    private String fullName;
    private String email;
    private String bio;
    private String status;
    private String currentPassword;
    private String newPassword;
}