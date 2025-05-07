package com.example.chatp2p.services;


import com.example.chatp2p.models.dto.ChatResponse;
import com.example.chatp2p.models.entitities.Chat;
import com.example.chatp2p.repositories.ChatRepository;
import com.example.chatp2p.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private MessageRepository messageRepository;

    public List<ChatResponse> getAllChatsByUserId(String uuid) {
        List<Chat> byUserIdsContaining = chatRepository.findByUserIdsContaining(uuid);
        List<ChatResponse> chatResponses = new ArrayList<>();
        for (Chat chat : byUserIdsContaining) {
            Integer notReadMessages = messageRepository.findByChatIdAndReadByNotContaining(chat.getId(), uuid).size();

            ChatResponse from = ChatResponse.getFrom(chat);
            from.setUnreadMessages(notReadMessages);
            chatResponses.add(from);
        }
        return chatResponses;
    }


}