package com.usth.controller;

import com.usth.entity.Comment;
import com.usth.model.ChatMessage;
import com.usth.repository.CommentRepository;
import com.usth.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final CommentRepository commentRepository; // Thêm cái này để lấy lịch sử

    // 1 WebSocket: Nhận tin nhắn mới và bắn Realtime (Giữ nguyên)
    @MessageMapping("/chat/{locationId}/sendMessage")
    @SendTo("/topic/{locationId}")
    public ChatMessage sendMessage(@DestinationVariable String locationId, @Payload ChatMessage chatMessage) {
        return chatService.saveComment(locationId, chatMessage);
    }

    @MessageMapping("/chat/{locationId}/addUser")
    @SendTo("/topic/{locationId}")
    public ChatMessage addUser(@DestinationVariable String locationId, @Payload ChatMessage chatMessage) {
        chatMessage.setContent("đã tham gia thảo luận.");
        chatMessage.setType("JOIN");
        return chatMessage;
    }

    // 2 REST API: Lấy danh sách bình luận cũ
    @GetMapping("/api/chat/history/{locationId}")
    @ResponseBody // Trả về JSON
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable Long locationId) {
        List<Comment> comments = commentRepository.findByLocationId(locationId);

        // Chuyển đổi từ Entity Comment sang Model ChatMessage để Frontend dễ hiển thị
        List<ChatMessage> history = comments.stream().map(comment -> {
            ChatMessage msg = new ChatMessage();
            msg.setSender(comment.getUser().getUsername()); // Lấy tên người dùng
            msg.setContent(comment.getContent());
            msg.setType("CHAT");
            return msg;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(history);
    }
}