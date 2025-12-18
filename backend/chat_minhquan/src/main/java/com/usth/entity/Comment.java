package com.usth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    // Ai chat?
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Chat ở đâu? (Quan trọng: Nối với bảng Location của Sensor/API)
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}