package com.example.chatp2p.repositories;

import com.example.chatp2p.models.entitities.Friend;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends MongoRepository<Friend, String> {
    @Query("{ $or: [ { 'user_id': ?0, 'friend_id': ?1 }, { 'user_id': ?1, 'friend_id': ?0 } ] }")
    Optional<Friend> findByUserIds(String userId1, String userId2);

    @Query("{ 'user_id': ?0, 'status': 'ACCEPTED' }")
    List<Friend> findAcceptedFriendsByUserId(String userId);

    @Query("{ 'friend_id': ?0, 'status': 'PENDING' }")
    List<Friend> findPendingFriendRequests(String userId);

    @Query("{ $or: [ { 'user_id': ?0 }, { 'friend_id': ?0 } ], 'status': 'ACCEPTED' }")
    List<Friend> findAllFriends(String userId);

    @Query("{ 'user_id': ?0, 'friend_id': ?1, 'status': 'PENDING' }")
    Optional<Friend> findPendingRequest(String userId, String friendId);

    List<Friend> findByUserIdAndStatus(String userId, Friend.Status status);

    List<Friend> findByFriendIdAndStatus(String friendId, Friend.Status status);

    Friend findByUserIdAndFriendId(String userId, String friendId);

    Friend findByUserIdAndFriendIdAndStatus(String userId, String friendId, Friend.Status status);
} 