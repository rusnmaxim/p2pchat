package com.example.chatp2p.services;

import com.example.chatp2p.models.dto.PicturePost;
import com.example.chatp2p.models.dto.UpdateProfileRequest;
import com.example.chatp2p.models.entitities.User;
import com.example.chatp2p.models.entitities.UserProfile;
import com.example.chatp2p.repositories.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;

    @Override
    public UserProfile getUserProfileFor(String uuid) {
        User user = userService.searchUsersById(uuid);
        Optional<UserProfile> profile = userProfileRepository.findById(uuid);
        UserProfile userProfile = profile.get();
        userProfile.setFullName(user.getFullName());
        userProfile.setEmail(user.getEmail());
        userProfile.setFullName(user.getFullName());
        return userProfile;
    }

    @Override
    public UserProfile updateProfile(OidcUser user, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findById(user.getSubject())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setId(user.getSubject());
                    return newProfile;
                });

        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }

        if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
            System.out.println("Change password request!" + request.getCurrentPassword());
            userService.changePassword(user.getSubject(), request.getNewPassword(), user.getEmail(), request.getCurrentPassword());
        }

        userService.updateUserProfile(user.getUserInfo().getPreferredUsername(), request.getFullName(), "", request.getEmail());
        return userProfileRepository.save(profile);

    }

    @Override
    public PicturePost uploadProfilePicture(OidcUser user, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new NoSuchElementException("The file was not provided");
        }
        String filename = fileStorageService.storeFile(file);
        UserProfile profile = userProfileRepository.findById(user.getSubject())
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setId(user.getSubject());
                    newProfile.setEmail(user.getEmail());
                    return newProfile;
                });

        String pictureUrl = "/api/files/" + filename;
        profile.setPictureUrl(pictureUrl);
        userProfileRepository.save(profile);

        return new PicturePost(pictureUrl);

    }
}
