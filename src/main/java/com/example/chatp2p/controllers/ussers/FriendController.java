package com.example.chatp2p.controllers.ussers;

import com.example.chatp2p.models.entitities.User;
import com.example.chatp2p.services.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private FriendService friendService;

    @GetMapping
    public ResponseEntity<List<User>> getFriends(Authentication authentication) {
        String userId = getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingRequests(Authentication authentication) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            System.out.println("Getting pending requests for user: " + userId);
            List<User> requests = friendService.getPendingRequests(userId);
            System.out.println("Found " + requests.size() + " pending requests");
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            System.err.println("Error getting pending requests: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(Authentication authentication, @RequestBody String friendId) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            friendService.sendFriendRequest(userId, friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(Authentication authentication, @RequestBody String friendId) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            friendService.acceptFriendRequest(userId, friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectFriendRequest(Authentication authentication, @RequestBody String friendId) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            friendService.rejectFriendRequest(userId, friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<?> removeFriend(Authentication authentication, @PathVariable String friendId) {
        try {
            String userId = getUserIdFromAuthentication(authentication);
            friendService.removeFriend(userId, friendId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(friendService.searchUsers(query));
    }

    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            return oidcUser.getSubject();
        }
        throw new IllegalStateException("User not authenticated");
    }
} 