package com.example.chatp2p.controllers;

import com.example.chatp2p.models.dto.AddUsersRequest;
import com.example.chatp2p.models.dto.ChatResponse;
import com.example.chatp2p.models.dto.CreateChatRequest;
import com.example.chatp2p.models.dto.UpdateMessageRequest;
import com.example.chatp2p.models.entitities.Chat;
import com.example.chatp2p.models.entitities.Message;
import com.example.chatp2p.models.entitities.User;
import com.example.chatp2p.models.entitities.UserProfile;
import com.example.chatp2p.repositories.ChatRepository;
import com.example.chatp2p.repositories.MessageRepository;
import com.example.chatp2p.repositories.UserProfileRepository;
import com.example.chatp2p.services.ChatService;
import com.example.chatp2p.services.FileStorageService;
import com.example.chatp2p.services.UserService;
import jakarta.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    ChatService chatService;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;


    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;


    @GetMapping
    public List<ChatResponse> getUserChats(@AuthenticationPrincipal OidcUser user) {
        return chatService.getAllChatsByUserId(user.getSubject());
    }

    @PostMapping
    public Chat createChat(@AuthenticationPrincipal OidcUser user, @RequestBody CreateChatRequest request) {
        Chat chat = new Chat(request.getName(), user.getSubject());
        chat.getUserIds().add(user.getSubject());
        chat.getUserIds().addAll(request.getUserIds());
        if (request.getUserIds() != null) {
            chat.getUserIds().addAll(request.getUserIds());
        }
        return chatRepository.save(chat);
    }

    @GetMapping("/{chatId}")
    public Chat getChat(@PathVariable String chatId) {
        return chatRepository.findById(chatId).orElseThrow();
    }

    @GetMapping("/check-name")
    public Boolean checkChatName(@QueryParam("name") String name) {
        Optional<Chat> id = chatRepository.findById(name);
        return id.isPresent();
    }

    @PostMapping("/{chatId}/users")
    public ResponseEntity<?> addUsersToChat(@PathVariable String chatId, @RequestBody AddUsersRequest request) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        chat.getUserIds().addAll(request.getUserIds());
        chatRepository.save(chat);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatId}/messages")
    public List<Message> getChatMessages(
            @PathVariable String chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtDesc(chatId, pageable);
        for (Message message : messages) {
            User sender = userService.searchUsersById(message.getSenderId());
            if (sender != null) {
                message.setSenderUsername(sender.getUsername());
            }
        }
        return messages;
    }

    @PostMapping(value = "/{chatId}/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> sendMessage(
            @PathVariable String chatId,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<MultipartFile> files,
            @AuthenticationPrincipal OidcUser user) {
        try {
            User dbUser = userService.searchUsersById(user.getSubject());
            if (dbUser == null) {
                throw new RuntimeException("User not found");
            }

            Message message = new Message();
            message.setContent(content);
            message.setSenderId(dbUser.getId());
            message.setSenderUsername(dbUser.getUsername());
            message.setChatId(chatId);
            message.setCreatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            message.setReadBy(List.of(user.getSubject()));
            message.setUpdatedAt(message.getCreatedAt());

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
                    }
                }
                message.setFileAttachments(attachments);
            }

            Message savedMessage = messageRepository.save(message);
            return ResponseEntity.ok(savedMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<?> deleteChat(@PathVariable String chatId, @AuthenticationPrincipal OidcUser user) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();

        if (!chat.getCreatedBy().equals(user.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only the chat creator can delete this chat");
        }

        chatRepository.delete(chat);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{chatId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable String chatId,
            @PathVariable String messageId,
            @AuthenticationPrincipal OidcUser user) {
        try {
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));

            if (!message.getChatId().equals(chatId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Message does not belong to this chat");
            }

            if (!message.getSenderId().equals(user.getSubject())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only delete your own messages");
            }

            if (message.getFileAttachments() != null) {
                for (Message.FileAttachment attachment : message.getFileAttachments()) {
                    try {
                        String filename = attachment.getFileUrl().substring("/api/files/".length());
                        fileStorageService.deleteFile(filename);
                    } catch (IOException e) {
                    }
                }
            }

            messageRepository.delete(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting message: " + e.getMessage());
        }
    }

    @GetMapping("/{chatId}/members")
    public ResponseEntity<List<User>> getChatMembers(@PathVariable String chatId, @AuthenticationPrincipal OidcUser user) {
        Chat chat = chatRepository.findById(chatId).orElseThrow();
        List<User> members = chat.getUserIds().stream()
                .map(userId -> userProfileRepository.findById(userId))
                .filter(Optional::isPresent)
                .map(opt -> convertToUser(opt.get())) // или .map(opt::get) если UserProfile == User
                .toList();
        return ResponseEntity.ok(members);
    }

    private User convertToUser(UserProfile profile) {
        User user = new User();
        User searchUsersById = userService.searchUsersById(profile.getId());
        user.setId(profile.getId());
        user.setPictureUrl(profile.getPictureUrl());
        user.setFullName(searchUsersById.getFullName());
        return user;
    }

    @GetMapping("/user/messages")
    public ResponseEntity<Map<String, List<Message>>> getUserMessages(
            @AuthenticationPrincipal OidcUser user,
            @RequestParam(required = false) String lastTimestamp) {
        try {
            List<Chat> userChats = chatRepository.findByUserIdsContaining(user.getSubject());

            Map<String, List<Message>> chatMessages = new HashMap<>();
            PageRequest pageable = PageRequest.of(0, 50);
            for (Chat chat : userChats) {
                List<Message> messages;
                if (lastTimestamp != null) {
                    messages = messageRepository.findByChatIdAndCreatedAtAfter(
                            chat.getId(),
                            LocalDateTime.parse(lastTimestamp)
                    );
                } else {
                    messages = messageRepository.findByChatIdOrderByCreatedAtDesc(
                            chat.getId(),
                            pageable
                    );
                }

                if (!messages.isEmpty()) {
                    chatMessages.put(chat.getId(), messages);
                }
            }

            return ResponseEntity.ok(chatMessages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{chatId}/messages/read")
    public ResponseEntity<?> markMessagesAsRead(
            @PathVariable("chatId") String chatId,
            @AuthenticationPrincipal OidcUser user) {
        try {
            List<Message> unreadMessages = messageRepository.findByChatIdAndReadByNotContaining(chatId, user.getSubject());

            for (Message message : unreadMessages) {
                if (message.getReadBy() == null) {
                    message.setReadBy(new ArrayList<>());
                }
                if (!message.getReadBy().contains(user.getSubject())) {
                    message.getReadBy().add(user.getSubject());
                    messageRepository.save(message);
                }
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error marking messages as read: " + e.getMessage());
        }
    }

    @PutMapping("/{chatId}/messages/{messageId}")
    public ResponseEntity<?> updateMessage(
            @PathVariable String chatId,
            @PathVariable String messageId,
            @RequestBody UpdateMessageRequest request,
            @AuthenticationPrincipal OidcUser user) {
        try {
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new RuntimeException("Message not found"));


            if (!message.getChatId().equals(chatId)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Message does not belong to this chat");
            }


            if (!message.getSenderId().equals(user.getSubject())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only edit your own messages");
            }


            message.setContent(request.getContent());
            message.setUpdatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            Message updatedMessage = messageRepository.save(message);

            return ResponseEntity.ok(updatedMessage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating message: " + e.getMessage());
        }
    }

}