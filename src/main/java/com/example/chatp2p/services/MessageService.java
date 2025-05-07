package com.example.chatp2p.services;

import com.example.chatp2p.models.entitities.Message;
import com.example.chatp2p.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class MessageService {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private FileStorageService fileStorageService;

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });
    }

    public Message saveMessage(Message message) {
        message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        message.setUpdatedAt(message.getCreatedAt());
        Message savedMessage = messageRepository.save(message);

        // Broadcast the message to all connected clients
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(savedMessage);
            } catch (Exception e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }

        return savedMessage;
    }

    public Message sendMessage(String content, String senderId, String channelId, String teamId, boolean isRichText, List<MultipartFile> files) {
        Message message = new Message();
        message.setContent(content);
        message.setSenderId(senderId);
        message.setChannelId(channelId);
        message.setTeamId(teamId);
        message.setRichText(isRichText);

        if (files != null && !files.isEmpty()) {
            List<Message.FileAttachment> attachments = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    String filename = fileStorageService.storeFile(file);
                    Message.FileAttachment attachment = new Message.FileAttachment();
                    attachment.setFileName(file.getOriginalFilename());
                    attachment.setFileUrl("/api/files/" + filename);
                    attachment.setFileSize(file.getSize());
                    attachment.setMimeType(file.getContentType());
                    attachments.add(attachment);
                } catch (IOException e) {
                    // Log error and continue with other files
                    e.printStackTrace();
                }
            }
            message.setFileAttachments(attachments);
        }

        return saveMessage(message);
    }

    public Message replyToThread(String content, String senderId, String threadId, boolean isRichText, List<MultipartFile> files) {
        Message message = new Message();
        message.setContent(content);
        message.setSenderId(senderId);
        message.setThreadId(threadId);
        message.setRichText(isRichText);

        if (files != null && !files.isEmpty()) {
            List<Message.FileAttachment> attachments = new ArrayList<>();
            for (MultipartFile file : files) {
                try {
                    String filename = fileStorageService.storeFile(file);
                    Message.FileAttachment attachment = new Message.FileAttachment();
                    attachment.setFileName(file.getOriginalFilename());
                    attachment.setFileUrl("/api/files/" + filename);
                    attachment.setFileSize(file.getSize());
                    attachment.setMimeType(file.getContentType());
                    attachments.add(attachment);
                } catch (IOException e) {
                    // Log error and continue with other files
                    e.printStackTrace();
                }
            }
            message.setFileAttachments(attachments);
        }

        return saveMessage(message);
    }

    public List<Message> getChannelMessages(String channelId) {
        return messageRepository.findByChannelIdOrderByCreatedAtAsc(channelId);
    }

    public List<Message> getThreadMessages(String threadId) {
        return messageRepository.findByThreadIdOrderByCreatedAtAsc(threadId);
    }

    public Message addReaction(String messageId, String emoji, String userId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.getReactions().computeIfAbsent(emoji, k -> new ArrayList<>())
                    .add(userId);
            message.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            return messageRepository.save(message);
        }
        return message;
    }

    public Message removeReaction(String messageId, String emoji, String userId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null && message.getReactions().containsKey(emoji)) {
            message.getReactions().get(emoji).remove(userId);
            if (message.getReactions().get(emoji).isEmpty()) {
                message.getReactions().remove(emoji);
            }
            message.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            return messageRepository.save(message);
        }
        return message;
    }
}