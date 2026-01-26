package com.usth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users") // "user" hay bị trùng từ khóa SQL
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String fullName;
    private String userLocation; // Thành phố nơi user sống

    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(columnDefinition = "VARCHAR(255) DEFAULT 'USER'")
    @Builder.Default
    private String role = "USER";

    @Builder.Default
    private int warningCount = 0;
    @Builder.Default
    private int banCount = 0;
    private LocalDateTime banExpiration;
    @Builder.Default
    private boolean isBanned = false;

    @Builder.Default
    private Integer avatarId = 1; // Avatar ID (1-10)

    // Một User có thể comment nhiều lần
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore // Tránh vòng lặp vô tận khi xuất JSON
    private List<Comment> comments;
}