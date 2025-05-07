package com.example.chatp2p.models.entitities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "friends")
public class Friend {
    @Id
    private String id;

    @Field("user_id")
    private String userId;

    @Field("friend_id")
    private String friendId;

    @Field("status")
    private Status status;

    @Field("created_at")
    private long createdAt;

    @Field("updated_at")
    private long updatedAt;

    public Friend() {
    }

    public Friend(String userId, String friendId) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = Status.PENDING;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = System.currentTimeMillis();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }
} 