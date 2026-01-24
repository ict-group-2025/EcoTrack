package com.usth.controller;

import com.usth.dto.ChatMessage;
import com.usth.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 1 WebSocket: Nhận tin nhắn mới và bắn Realtime
    @MessageMapping("/chat/{locationId}/sendMessage")
    @SendTo("/topic/{locationId}")
    public ChatMessage sendMessage(@DestinationVariable String locationId, @Payload ChatMessage chatMessage) {
        try {
            if (chatMessage == null || chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
                log.warn("Tin nhắn rỗng từ locationId: {}", locationId);
                return null;
            }
            return chatService.saveComment(locationId, chatMessage);
        } catch (Exception e) {
            log.error("Lỗi khi lưu tin nhắn từ locationId {}: {}", locationId, e.getMessage());
            return null;
        }
    }

    @MessageMapping("/chat/{locationId}/addUser")
    @SendTo("/topic/{locationId}")
    public ChatMessage addUser(@DestinationVariable String locationId, @Payload ChatMessage chatMessage) {
        chatMessage.setContent("đã tham gia thảo luận.");
        chatMessage.setType("JOIN");
        return chatMessage;
    }

    // 2 REST API: Lấy danh sách bình luận cũ (có pagination để tối ưu)
    @GetMapping("/api/chat/history/{locationId}")
    @ResponseBody
    public ResponseEntity<?> getChatHistory(
            @PathVariable Long locationId,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "50") int size) {
        try {
            return ResponseEntity.ok(chatService.getChatHistory(locationId, page, size));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}