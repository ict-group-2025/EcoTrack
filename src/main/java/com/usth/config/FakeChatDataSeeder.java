package com.usth.config;

import com.usth.entity.Comment;
import com.usth.entity.Location;
import com.usth.entity.User;
import com.usth.repository.CommentRepository;
import com.usth.repository.LocationRepository;
import com.usth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Order(2) // Chạy sau DataInitializer
public class FakeChatDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    // Danh sách fake users
    private static final String[][] FAKE_USERS = {
            { "weather_fan", "Thời Tiết Fan", "1" },
            { "rain_lover", "Mưa Yêu Thương", "2" },
            { "sunny_day", "Nắng Đẹp", "3" },
            { "cloud_watcher", "Mây Bay", "4" },
            { "storm_chaser", "Bão Hunter", "5" },
            { "eco_warrior", "Eco Warrior", "6" },
            { "nature_lover", "Thiên Nhiên", "7" },
            { "sky_observer", "Trời Xanh", "8" },
            { "wind_surfer", "Gió Mát", "9" },
            { "rainbow_hunter", "Cầu Vồng", "10" }
    };

    // Danh sách fake messages về thời tiết
    private static final String[] FAKE_MESSAGES = {
            "Hôm nay thời tiết đẹp quá! ☀️",
            "Trời mưa rồi, nhớ mang ô nhé mọi người 🌧️",
            "Nhiệt độ hôm nay dễ chịu lắm 😊",
            "Dự báo nói mai sẽ có mưa, chuẩn bị thôi!",
            "Chất lượng không khí hôm nay khá tốt 👍",
            "Gió mạnh quá, cẩn thận khi ra đường!",
            "Độ ẩm cao quá, hơi khó chịu 😅",
            "Thời tiết lý tưởng để đi dạo!",
            "Mây đen kéo đến rồi, sắp mưa to đấy ⛈️",
            "Nắng đẹp, ra ngoài tận hưởng thôi! 🌤️",
            "AQI hôm nay hơi cao, nên đeo khẩu trang",
            "Nhiệt độ giảm rồi, mặc ấm nhé mọi người!",
            "Thời tiết thay đổi liên tục, khó đoán quá 🤔",
            "Sáng mát mẻ, chiều nóng - điển hình mùa này",
            "Có ai thấy cầu vồng sau mưa không? 🌈",
            "Ô nhiễm không khí tăng, hạn chế ra ngoài!",
            "Thời tiết này đi picnic được đấy 🧺",
            "Mưa phùn lất phất, lãng mạn ghê!",
            "Nhiệt độ cao kỷ lục, uống nhiều nước nhé!",
            "Có bão đang đến, theo dõi tin tức thường xuyên!"
    };

    @Override
    public void run(String... args) throws Exception {
        // Chạy trong thread riêng để không block app
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Đợi DatabaseLoader load xong locations

                // 1. Tạo fake users nếu chưa có
                System.out.println(">>> Đang tạo fake users để test...");
                for (String[] userData : FAKE_USERS) {
                    if (!userRepository.existsByUsername(userData[0])) {
                        User user = User.builder()
                                .username(userData[0])
                                .fullName(userData[1])
                                .password(passwordEncoder.encode("123456"))
                                .email(userData[0] + "@test.com")
                                .role("USER")
                                .avatarId(Integer.parseInt(userData[2]))
                                .userLocation("Hà Nội")
                                .build();
                        userRepository.save(user);
                    }
                }
                System.out.println(">>> ✅ Đã tạo " + FAKE_USERS.length + " fake users");

                // 2. Lấy danh sách users và locations
                List<User> fakeUsers = userRepository.findAll().stream()
                        .filter(u -> !u.getRole().equals("ADMIN"))
                        .toList();
                List<Location> locations = locationRepository.findAll();

                if (locations.isEmpty() || fakeUsers.isEmpty()) {
                    System.out.println(">>> ⚠️ Chưa có locations hoặc users, bỏ qua fake chat");
                    return;
                }

                // 3. Kiểm tra xem đã có chat data chưa
                long existingComments = commentRepository.count();
                if (existingComments > 50) {
                    System.out.println(">>> ℹ️ Đã có " + existingComments + " comments, bỏ qua seed data");
                    return;
                }

                // 4. Tạo fake messages cho mỗi location
                System.out.println(">>> Đang tạo fake chat messages...");
                Random random = new Random();
                int totalMessages = 0;

                for (Location location : locations) {
                    // Mỗi location có 3-8 messages
                    int messageCount = 3 + random.nextInt(6);

                    for (int i = 0; i < messageCount; i++) {
                        User randomUser = fakeUsers.get(random.nextInt(fakeUsers.size()));
                        String randomMessage = FAKE_MESSAGES[random.nextInt(FAKE_MESSAGES.length)];

                        Comment comment = Comment.builder()
                                .content(randomMessage)
                                .user(randomUser)
                                .location(location)
                                .build();
                        commentRepository.save(comment);
                        totalMessages++;
                    }
                }

                System.out.println(">>> ✅ HOÀN TẤT: Đã tạo " + totalMessages + " fake messages cho " + locations.size()
                        + " phòng chat");
                System.out.println(">>> 📌 Test Admin: Ban user 'weather_fan' hoặc 'rain_lover' để xem hiệu ứng");

            } catch (Exception e) {
                System.err.println(">>> ❌ Lỗi tạo fake data: " + e.getMessage());
            }
        }).start();
    }
}
