package com.example.chatp2p.repositories;

import com.example.chatp2p.models.entitities.Message;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChannelIdOrderByCreatedAtAsc(String channelId);

    List<Message> findByThreadIdOrderByCreatedAtAsc(String threadId);

    List<Message> findByChatIdAndCreatedAtAfter(String chatId, LocalDateTime parse);

    List<Message> findByChatIdAndReadByNotContaining(String chatId, String redBy);

    List<Message> findByChatIdOrderByCreatedAtDesc(String chatId, PageRequest pageable);
}