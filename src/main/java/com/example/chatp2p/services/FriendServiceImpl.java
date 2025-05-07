package com.example.chatp2p.services;

import com.example.chatp2p.models.entitities.Friend;
import com.example.chatp2p.models.entitities.User;
import com.example.chatp2p.models.entitities.UserProfile;
import com.example.chatp2p.repositories.FriendRepository;
import com.example.chatp2p.repositories.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendServiceImpl implements FriendService {
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserService userService;

    @Override
    public List<User> getFriends(String userId) {
        List<Friend> friends = friendRepository.findByUserIdAndStatus(userId, Friend.Status.ACCEPTED);
        friends.addAll(friendRepository.findByFriendIdAndStatus(userId, Friend.Status.ACCEPTED));
        List<User> users = new ArrayList<>();
        for (Friend friend : friends) {
            if (friend.getUserId().equals(userId)) {
                User user = userService.searchUsersById(friend.getFriendId());
                user.setPictureUrl(userProfileRepository.findById(friend.getFriendId()).get().getPictureUrl());
                users.add(user);
            } else {
                User user = userService.searchUsersById(friend.getUserId());
                user.setPictureUrl(userProfileRepository.findById(friend.getUserId()).get().getPictureUrl());
                users.add(user);
            }
        }

        return users;
    }

    @Override
    public List<User> getPendingRequests(String userId) {
        System.out.println("FriendServiceImpl: Getting pending requests for user: " + userId);
        List<Friend> receivedRequests = friendRepository.findByFriendIdAndStatus(userId, Friend.Status.PENDING);

        List<String> requestUserIds = receivedRequests.stream()
                .map(Friend::getUserId)
                .collect(Collectors.toList());
        List<User> users = requestUserIds.stream()
                .filter(user -> user != null)
                .map(el -> {
                    User user = new User();
                    Optional<UserProfile> byId = userProfileRepository.findById(el);
                    user.setPictureUrl(byId.get().getPictureUrl());
                    user.setId(byId.get().getId());
                    user.setUsername(byId.get().getEmail());
                    return user;
                })
                .collect(Collectors.toList());

        System.out.println("Returning " + users.size() + " users with pending requests");
        return users;
    }

    @Override
    public Friend sendFriendRequest(String userId, String friendId) {
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Cannot send friend request to yourself");
        }

        Friend existingRequest = friendRepository.findByUserIdAndFriendId(userId, friendId);
        if (existingRequest != null) {
            throw new IllegalStateException("Friend request already exists");
        }

        Friend request = new Friend(userId, friendId);
        return friendRepository.save(request);
    }

    @Override
    public Friend acceptFriendRequest(String userId, String friendId) {
        Friend request = friendRepository.findByUserIdAndFriendIdAndStatus(friendId, userId, Friend.Status.PENDING);
        if (request == null) {
            throw new IllegalStateException("No pending friend request found");
        }

        request.setStatus(Friend.Status.ACCEPTED);
        return friendRepository.save(request);
    }

    @Override
    public Friend rejectFriendRequest(String userId, String friendId) {
        Friend request = friendRepository.findByUserIdAndFriendIdAndStatus(friendId, userId, Friend.Status.PENDING);
        if (request == null) {
            throw new IllegalStateException("No pending friend request found");
        }

        request.setStatus(Friend.Status.REJECTED);
        return friendRepository.save(request);
    }

    @Override
    public void removeFriend(String userId, String friendId) {
        Friend friendship = friendRepository.findByUserIdAndFriendIdAndStatus(friendId, userId, Friend.Status.ACCEPTED);
        Friend friendship2 = friendRepository.findByUserIdAndFriendIdAndStatus(userId, friendId, Friend.Status.ACCEPTED);
        if (friendship != null) {
            friendRepository.delete(friendship);
        }
        if (friendship2 != null) {
            friendRepository.delete(friendship2);
        }
    }

    @Override
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        return null;
    }
} 