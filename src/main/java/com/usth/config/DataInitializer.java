package com.usth.config;

import com.usth.entity.User;
import com.usth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Luôn đảm bảo tài khoản Admin tồn tại và mật khẩu đúng
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .fullName("Administrator")
                    .password(passwordEncoder.encode("123456"))
                    .email("admin@ecotrack.com") // Thêm email dummy để tránh null nếu cần
                    .role("ADMIN")
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Đã tạo tài khoản ADMIN mặc định: admin / 123456");
        } else {
            // Reset password nếu tài khoản đã tồn tại (để sửa lỗi login)
            User admin = userRepository.findByUsername("admin").get();
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("ADMIN"); // Đảm bảo role đúng
            userRepository.save(admin);
            System.out.println(">>> Đã cập nhật mật khẩu ADMIN: admin / 123456");
        }
    }
}
