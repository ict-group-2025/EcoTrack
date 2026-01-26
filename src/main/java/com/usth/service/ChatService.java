package com.usth.service;

import com.usth.entity.Comment;
import com.usth.entity.Location;
import com.usth.entity.User;
import com.usth.dto.ChatMessage;
import com.usth.repository.CommentRepository;
import com.usth.repository.LocationRepository;
import com.usth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings({ "nullness", "NullAway" })
public class ChatService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @SuppressWarnings("nullness")
    @Transactional
    public ChatMessage saveComment(String locationIdStr, ChatMessage chatMessage) {
        if (chatMessage == null || chatMessage.getSender() == null || chatMessage.getContent() == null) {
            throw new IllegalArgumentException("ChatMessage không hợp lệ");
        }

        try {
            // 1. Giả lập User (Trong thực tế sẽ lấy từ Token đăng nhập)
            String username = chatMessage.getSender().trim();
            if (username.isEmpty()) {
                throw new IllegalArgumentException("Username không được để trống");
            }

            User user = userRepository.findByUsername(username)
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .username(username)
                                .fullName(username)
                                .build();
                        return Objects.requireNonNull(userRepository.save(newUser), "Saved user cannot be null");
                    });

            // 2. Tìm Location theo ID
            Long locId;
            try {
                locId = Long.parseLong(locationIdStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Location ID không hợp lệ: " + locationIdStr);
            }

            Location location = locationRepository.findById(locId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Location với ID: " + locId));

            // 3. Validate và lưu Comment
            String content = chatMessage.getContent().trim();
            if (content.isEmpty()) {
                throw new IllegalArgumentException("Nội dung comment không được để trống");
            }

            // Giới hạn độ dài comment để tránh spam
            if (content.length() > 1000) {
                content = content.substring(0, 1000);
                log.warn("Comment quá dài, đã cắt bớt từ user: {}", username);
            }

            Comment comment = Comment.builder()
                    .content(content)
                    .user(user)
                    .location(location)
                    .build();

            Comment savedComment = commentRepository.save(comment);
            Objects.requireNonNull(savedComment, "Saved comment cannot be null");
            log.debug("Đã lưu comment từ user {} cho location {}", username, locId);

            chatMessage.setUserId(user.getId());
            chatMessage.setUserLocation(user.getUserLocation()); // Trả về location của user
            chatMessage.setAvatarId(user.getAvatarId()); // Avatar ID
            return chatMessage;
        } catch (IllegalArgumentException e) {
            log.error("Lỗi validation khi lưu comment: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Lỗi khi lưu comment: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể lưu comment: " + e.getMessage());
        }
    }

    public Map<String, Object> getChatHistory(Long locationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> commentPage = commentRepository.findByLocationIdWithUser(locationId, pageable);

        // Chuyển đổi từ Entity Comment sang Model ChatMessage
        List<ChatMessage> history = commentPage.getContent().stream().map(comment -> {
            ChatMessage msg = new ChatMessage();
            if (comment.getUser() != null) {
                msg.setSender(comment.getUser().getUsername());
                msg.setUserId(comment.getUser().getId());
                msg.setUserLocation(comment.getUser().getUserLocation()); // Map location
                msg.setAvatarId(comment.getUser().getAvatarId()); // Avatar ID
            } else {
                msg.setSender("Unknown");
                msg.setAvatarId(1); // Default avatar
            }
            msg.setContent(comment.getContent());
            msg.setType("CHAT");
            return msg;
        }).collect(Collectors.toList());

        // Đóng gói response
        Map<String, Object> response = new HashMap<>();
        response.put("content", history);
        response.put("totalElements", commentPage.getTotalElements());
        response.put("totalPages", commentPage.getTotalPages());
        response.put("currentPage", commentPage.getNumber());
        response.put("pageSize", commentPage.getSize());

        return response;
    }
}