package com.example.chatp2p.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserGet {
    private String id;
    private String username;
    private String email;
    private String name;
}
