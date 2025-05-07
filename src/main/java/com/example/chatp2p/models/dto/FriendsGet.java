package com.example.chatp2p.models.dto;

import com.example.chatp2p.models.entitities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FriendsGet {
    private String id;
    private String username;
    private String email;

    public static List<FriendsGet> getListFrom(List<User> list) {
        return list.stream()
                .map(u ->
                        new FriendsGet(u.getId(), u.getUsername(), u.getEmail()))
                .toList();
    }
}
