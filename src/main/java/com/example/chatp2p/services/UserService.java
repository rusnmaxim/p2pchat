package com.example.chatp2p.services;

import com.example.chatp2p.models.entitities.User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> searchUsers(String query, String uuid);

    User searchUsersById(String query);

    void changePassword(String uuid, String password, String username, String oldPassword);

    void updateUserProfile(String username, String newFirstName, String newLastName, String newEmail);

    void updateStatus(OidcUser oidcUser);

    Map<String, Boolean> isOnline(List<String> userIds);
}