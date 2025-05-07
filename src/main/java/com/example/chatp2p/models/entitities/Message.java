package com.example.chatp2p.models.entitities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

@Document(collection = "messages")
@Data
public class Message {
    @Id
    private String id;

    @Field("content")
    private String content;

    @Field("sender_id")
    private String senderId;

    @Field("sender_username")
    private String senderUsername;

    @Field("channel_id")
    private String channelId;

    @Field("team_id")
    private String teamId;

    @Field("thread_id")
    private String threadId;

    @Field("chat_id")
    private String chatId;

    @Field("is_rich_text")
    private boolean richText;

    @Field("created_at")
    private String createdAt;

    @Field("updated_at")
    private String updatedAt;

    @Field("reactions")
    private Map<String, List<String>> reactions;

    @Field("file_attachments")
    private List<FileAttachment> fileAttachments;

    @Field("read_by")
    private List<String> readBy;

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    @Data
    public static class FileAttachment {
        @Field("file_name")
        private String fileName;

        @Field("file_url")
        private String fileUrl;

        @Field("file_size")
        private long fileSize;

        @Field("mime_type")
        private String mimeType;
    }
} 