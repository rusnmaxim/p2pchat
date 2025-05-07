package com.example.chatp2p.services;

import com.example.chatp2p.models.entitities.Friend;
import com.example.chatp2p.models.entitities.User;

import java.util.List;


public interface FriendService {


    public List<User> getFriends(String userId);

    public List<User> getPendingRequests(String userId);


    public Friend sendFriendRequest(String userId, String friendId);


    public Friend acceptFriendRequest(String userId, String friendId);

    public Friend rejectFriendRequest(String userId, String friendId);

    public void removeFriend(String userId, String friendId);

    public List<User> searchUsers(String query);
}