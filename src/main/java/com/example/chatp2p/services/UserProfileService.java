package com.example.chatp2p.services;

import com.example.chatp2p.models.dto.PicturePost;
import com.example.chatp2p.models.dto.UpdateProfileRequest;
import com.example.chatp2p.models.entitities.UserProfile;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserProfileService {
    UserProfile getUserProfileFor(String uuid);

    UserProfile updateProfile(OidcUser user, UpdateProfileRequest request);

    PicturePost uploadProfilePicture(OidcUser user, MultipartFile file) throws IOException;
}
