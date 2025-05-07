package com.example.chatp2p.models.entitities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@NoArgsConstructor
public class User {
    private String id;


    private String username;

    @Field("email")
    private String email;

    private boolean isOnline = false;

    private String fullName;

    private String pictureUrl;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
