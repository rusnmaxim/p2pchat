package com.example.chatp2p.services;

import com.example.chatp2p.models.entitities.User;
import com.example.chatp2p.models.entitities.UserProfile;
import com.example.chatp2p.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserProfileRepository userProfileRepository;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}")
    private String clientSecret;


    private Keycloak getKeycloakInstance() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakServerUrl)
                .realm(realm)
                .clientId("adm")
                .clientSecret("G7iV6plbGcn5WAi0uqrn2eHRolwprBKG")
                .grantType("client_credentials")
                .build();
    }


    @Override
    public List<User> searchUsers(String query, String uuid) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }

        try (Keycloak keycloak = getKeycloakInstance()) {

            List<UserRepresentation> keycloakUsers = keycloak.realm("chatrealm")
                    .users()
                    .searchByEmail(query, true);

            return keycloakUsers.stream()
                    .map(this::convertToUser).distinct()
                    .filter(el -> !el.getId().equals(uuid))
                    .peek(el -> {
                        Optional<UserProfile> byId = userProfileRepository.findById(el.getId());
                        el.setPictureUrl(byId.get().getPictureUrl());
                        el.setOnline(isOnline(List.of(el.getId())).get(el.getId()));
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching users in Keycloak: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public User searchUsersById(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return null;
        }

        try (Keycloak keycloak = getKeycloakInstance()) {
            String searchQuery = uuid.trim();
            Set<User> results = new HashSet<>();

            UserRepresentation keycloakUsers = keycloak.realm("chatrealm")
                    .users()
                    .get(uuid).toRepresentation();


            User user = convertToUser(keycloakUsers);

            return user;
        } catch (Exception e) {
            System.err.println("Error searching users in Keycloak: " + e.getMessage());
            return null;
        }
    }

    public void changePassword(String uuid, String password, String username, String oldPassword) {
        try (Keycloak keycloak = getKeycloakInstance()) {
            if (validateOldPassword(keycloakServerUrl, realm, clientId, username, oldPassword)) {
                UserResource userResource = keycloak.realm("chatrealm")
                        .users()
                        .get(uuid);

                CredentialRepresentation newCredential = new CredentialRepresentation();
                newCredential.setType(CredentialRepresentation.PASSWORD);
                newCredential.setValue(password);
                newCredential.setTemporary(false);

                userResource.resetPassword(newCredential);
            }
        } catch (Exception e) {
            System.err.println("Error searching users in Keycloak: " + e.getMessage());
        }
    }

    public void updateUserProfile(String username, String newFirstName, String newLastName, String newEmail) {
        try (Keycloak keycloak = getKeycloakInstance()) {

            UserRepresentation user = keycloak.realm(realm)
                    .users()
                    .search(username)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            user.setFirstName(newFirstName);
            user.setLastName(newLastName);
            user.setEmail(newEmail);

            UserResource userResource = keycloak.realm(realm).users().get(user.getId());

            userResource.update(user);
        } catch (Exception e) {
            System.err.println("Error searching users in Keycloak: " + e.getMessage());
        }
    }

    @Override
    public void updateStatus(OidcUser oidcUser) {
        Optional<UserProfile> byId = userProfileRepository.findById(oidcUser.getSubject());
        UserProfile userProfile = byId.get();
        userProfile.setStatus("online");
        userProfile.setStatus_timestamp(System.currentTimeMillis());
        userProfileRepository.save(userProfile);
    }

    @Override
    public Map<String, Boolean> isOnline(List<String> userIds) {
        Map<String, Boolean> hash = new HashMap<>();

        for (String userId : userIds) {
            Optional<UserProfile> byId = userProfileRepository.findById(userId);
            UserProfile userProfile = byId.get();
            if (userProfile.getStatus().equals("online") && userProfile.getStatus_timestamp() > System.currentTimeMillis() - 30000) {
                hash.put(userId, true);
            } else {
                hash.put(userId, false);
            }

        }
        return hash;
    }

    public boolean validateOldPassword(String serverUrl, String realm, String clientId, String username, String oldPassword) throws IOException {
        String url = serverUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        String params = "grant_type=password"
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(oldPassword, StandardCharsets.UTF_8);
        System.out.println(params);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setDoOutput(true);
        try (OutputStream os = con.getOutputStream()) {
            byte[] input = params.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        System.out.println(responseCode);
        return responseCode == 200;
    }

    private User convertToUser(UserRepresentation keycloakUser) {
        User user = new User(
                keycloakUser.getUsername(),
                keycloakUser.getEmail()
        );
        user.setId(keycloakUser.getId());
        user.setFullName(keycloakUser.getFirstName() + " " + keycloakUser.getLastName());
        return user;
    }
} 