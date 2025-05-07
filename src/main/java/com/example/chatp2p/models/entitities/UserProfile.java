package com.example.chatp2p.models.entitities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user_profiles")
public class UserProfile {
    @Id
    private String id;
    private String fullName;
    private String username;
    private String email;
    private String bio;
    private String status;
    private long status_timestamp;
    private String pictureUrl;
} 