package com.example.chatp2p.repositories;

import com.example.chatp2p.models.entitities.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findByUserIdsContaining(String userId);

    List<Chat> findByCreatedBy(String userId);
}