package com.example.chatp2p.models.dto;

import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Data
public class UserDTO {
    private String fullName;
    private String email;
    private String password;
    @JsonIgnore
    private String role = "user";
}