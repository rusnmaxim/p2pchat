package com.example.chatp2p.models.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRequest {
    private String name;
    private Set<String> userIds;
}
