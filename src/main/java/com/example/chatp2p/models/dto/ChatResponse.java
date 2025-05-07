package com.example.chatp2p.models.dto;

import com.example.chatp2p.models.entitities.Chat;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class ChatResponse {
    private String id;
    private String name;
    private Set<String> userIds = new HashSet<>();
    private String createdBy;
    private Integer unreadMessages;
    private long createdAt;

    public static ChatResponse getFrom(Chat chat) {
        ChatResponse chatResponse = new ChatResponse();
        chatResponse.setId(chat.getId());
        chatResponse.setName(chat.getName());
        chatResponse.setUserIds(chat.getUserIds());
        chatResponse.setCreatedAt(chat.getCreatedAt());
        chatResponse.setCreatedBy(chat.getCreatedBy());
        return chatResponse;
    }
}
