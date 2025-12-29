package com.usth.service;

import com.usth.entity.Comment;
import com.usth.entity.Location;
import com.usth.entity.User;
import com.usth.model.ChatMessage;
import com.usth.repository.CommentRepository;
import com.usth.repository.LocationRepository;
import com.usth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public ChatMessage saveComment(String locationIdStr, ChatMessage chatMessage) {
        // 1. Giả lập User (Trong thực tế sẽ lấy từ Token đăng nhập)
        User user = userRepository.findByUsername(chatMessage.getSender())
                .orElseGet(() -> userRepository.save(User.builder()
                        .username(chatMessage.getSender())
                        .fullName(chatMessage.getSender())
                        .build()));

        // 2. Tìm Location theo ID
        Long locId = Long.parseLong(locationIdStr);
        Location location = locationRepository.findById(locId)
                .orElseThrow(() -> new RuntimeException("Location not found with ID: " + locId));

        // 3. Lưu Comment
        Comment comment = Comment.builder()
                .content(chatMessage.getContent())
                .user(user)
                .location(location)
                .build();

        commentRepository.save(comment);

        return chatMessage;
    }
}