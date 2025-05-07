package com.example.chatp2p.repositories;

import com.example.chatp2p.models.entitities.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByUsername(String username);

    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findById(String id);

}