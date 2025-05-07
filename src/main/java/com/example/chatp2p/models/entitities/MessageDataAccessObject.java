package com.example.chatp2p.models.entitities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
@Data
public class MessageDataAccessObject {

    @Id
    private String id;
    private String chatId;
    private String recipient;
    private String content;
    private long timestamp;

    public MessageDataAccessObject() {
    }

    public MessageDataAccessObject(String chatId, String recipient, String content, long timestamp) {
        this.chatId = chatId;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }
}