package com.example.chatp2p.controllers.ussers;

import com.example.chatp2p.models.dto.UpdateProfileRequest;
import com.example.chatp2p.models.entitities.UserProfile;
import com.example.chatp2p.services.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserProfileService userProfileService;


    @GetMapping
    public ResponseEntity<UserProfile> getProfile(@AuthenticationPrincipal OidcUser user) {
        return ResponseEntity.ok(userProfileService.getUserProfileFor(user.getSubject()));
    }

    @PutMapping
    public ResponseEntity<UserProfile> updateProfile(@AuthenticationPrincipal OidcUser user, @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(user, request));
    }

    @PostMapping("/picture")
    public ResponseEntity<?> uploadProfilePicture(
            @AuthenticationPrincipal OidcUser user,
            @RequestParam("picture") MultipartFile file) throws IOException {
        return ResponseEntity.ok(userProfileService.uploadProfilePicture(user, file));
    }

} 