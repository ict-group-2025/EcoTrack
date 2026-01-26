package com.usth.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity lưu lịch sử hành động của Admin
 * - Warn, Ban, Unban users
 * - Delete comments
 */
@Entity
@Table(name = "admin_logs")
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "admin_username", length = 100)
    private String adminUsername;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // WARN, BAN, UNBAN, DELETE_COMMENT

    @Column(name = "target_user_id")
    private Long targetUserId;

    @Column(name = "target_username", length = 100)
    private String targetUsername;

    @Column(name = "target_message_id")
    private Long targetMessageId;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public AdminLog() {
    }

    public AdminLog(Long adminId, String adminUsername, String actionType) {
        this.adminId = adminId;
        this.adminUsername = adminUsername;
        this.actionType = actionType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public Long getTargetMessageId() {
        return targetMessageId;
    }

    public void setTargetMessageId(Long targetMessageId) {
        this.targetMessageId = targetMessageId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
