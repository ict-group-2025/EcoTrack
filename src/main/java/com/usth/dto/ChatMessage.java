package com.usth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String sender;
    private Long userId;
    private String userLocation;
    private Integer avatarId; // Avatar ID (1-10)
    private String content;
    private String type;
}
