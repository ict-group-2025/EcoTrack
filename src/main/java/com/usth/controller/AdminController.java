package com.usth.controller;

import com.usth.entity.User;
import com.usth.repository.CommentRepository;
import com.usth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    // 1. Xóa bình luận
    @DeleteMapping("/comments/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        if (!commentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        commentRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa bình luận thành công.");
    }

    // 2. Cảnh báo user (Logic Ban tự động)
    @PostMapping("/users/{userId}/warn")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> warnUser(@PathVariable Long userId) {
        return userRepository.findById(userId).map(user -> {
            // Không thể cảnh báo admin khác
            if ("ADMIN".equals(user.getRole())) {
                return ResponseEntity.badRequest().body("Không thể cảnh báo Admin!");
            }

            user.setWarningCount(user.getWarningCount() + 1);
            String message = "Đã cảnh báo user. Số lần cảnh báo: " + user.getWarningCount();

            // Logic Ban
            if (user.getWarningCount() >= 3) {
                user.setWarningCount(0); // Reset cảnh báo
                user.setBanCount(user.getBanCount() + 1);

                if (user.getBanCount() == 1) {
                    user.setBanExpiration(LocalDateTime.now().plusDays(7));
                    message = "User bị cấm 1 tuần (Lần đầu).";
                } else if (user.getBanCount() == 2) {
                    user.setBanExpiration(LocalDateTime.now().plusMonths(1));
                    message = "User bị cấm 1 tháng (Lần 2).";
                } else {
                    user.setBanned(true);
                    user.setBanExpiration(null);
                    message = "User bị cấm vĩnh viễn (Lần 3).";
                }
            }

            userRepository.save(user);
            return ResponseEntity.ok(message);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 3. Ban User ngay lập tức
    @PostMapping("/users/{userId}/ban")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> banUser(@PathVariable Long userId) {
        return userRepository.findById(userId).map(user -> {
            if ("ADMIN".equals(user.getRole())) {
                return ResponseEntity.badRequest().body("Không thể ban Admin!");
            }
            user.setBanned(true);
            user.setBanExpiration(null); // Ban vĩnh viễn
            userRepository.save(user);
            return ResponseEntity.ok("Đã ban vĩnh viễn user " + user.getUsername());
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Unban User
    @PostMapping("/users/{userId}/unban")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> unbanUser(@PathVariable Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setBanned(false);
            user.setBanExpiration(null);
            user.setWarningCount(0); // Reset cảnh báo
            userRepository.save(user);
            return ResponseEntity.ok("Đã gỡ ban cho user " + user.getUsername());
        }).orElse(ResponseEntity.notFound().build());
    }
}
