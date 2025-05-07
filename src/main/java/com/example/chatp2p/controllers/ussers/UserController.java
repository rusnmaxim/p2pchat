package com.example.chatp2p.controllers.ussers;

import com.example.chatp2p.models.dto.FriendsGet;
import com.example.chatp2p.models.dto.UserGet;
import com.example.chatp2p.models.entitities.User;
import com.example.chatp2p.services.FriendService;
import com.example.chatp2p.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<FriendsGet>> getAvailableUsers(@AuthenticationPrincipal OidcUser user) {
        List<User> users = friendService.getFriends(user.getSubject());
        return ResponseEntity.ok(FriendsGet.getListFrom(users));
    }


    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String email, @AuthenticationPrincipal OidcUser user) {
        List<User> users = userService.searchUsers(email, user.getSubject());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<UserGet> getCurrentUser(@AuthenticationPrincipal OAuth2User user) {
        return ResponseEntity.ok(new UserGet(user.getAttribute("sub"), user.getAttribute("preferred_username"), user.getAttribute("email"), user.getAttribute("name")));
    }

    @PutMapping("status/update")
    public void getStatusOfUser(@AuthenticationPrincipal OidcUser user) {
        userService.updateStatus(user);
    }

    @PostMapping("status")
    public ResponseEntity<Map<String, Boolean>> isOnline(@RequestBody List<String> userIds) {
        return ResponseEntity.ok(userService.isOnline(userIds));
    }
}