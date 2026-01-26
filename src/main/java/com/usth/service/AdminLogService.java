package com.usth.service;

import com.usth.entity.AdminLog;
import com.usth.entity.User;
import com.usth.repository.AdminLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service ghi log các hành động của Admin
 */
@Service
public class AdminLogService {

    @Autowired
    private AdminLogRepository adminLogRepository;

    /**
     * Ghi log hành động WARN
     */
    public void logWarn(User admin, User targetUser) {
        AdminLog log = new AdminLog(admin.getId(), admin.getUsername(), "WARN");
        log.setTargetUserId(targetUser.getId());
        log.setTargetUsername(targetUser.getUsername());
        log.setDetails(
                "Cảnh báo user " + targetUser.getUsername() + " (warnings: " + targetUser.getWarningCount() + ")");
        adminLogRepository.save(log);
    }

    /**
     * Ghi log hành động BAN
     */
    public void logBan(User admin, User targetUser) {
        AdminLog log = new AdminLog(admin.getId(), admin.getUsername(), "BAN");
        log.setTargetUserId(targetUser.getId());
        log.setTargetUsername(targetUser.getUsername());
        log.setDetails("Ban vĩnh viễn user " + targetUser.getUsername());
        adminLogRepository.save(log);
    }

    /**
     * Ghi log hành động UNBAN
     */
    public void logUnban(User admin, User targetUser) {
        AdminLog log = new AdminLog(admin.getId(), admin.getUsername(), "UNBAN");
        log.setTargetUserId(targetUser.getId());
        log.setTargetUsername(targetUser.getUsername());
        log.setDetails("Gỡ ban user " + targetUser.getUsername());
        adminLogRepository.save(log);
    }

    /**
     * Ghi log hành động DELETE_COMMENT
     */
    public void logDeleteComment(User admin, Long messageId) {
        AdminLog log = new AdminLog(admin.getId(), admin.getUsername(), "DELETE_COMMENT");
        log.setTargetMessageId(messageId);
        log.setDetails("Xóa tin nhắn ID: " + messageId);
        adminLogRepository.save(log);
    }

    /**
     * Lấy 50 log gần đây nhất
     */
    public List<AdminLog> getRecentLogs() {
        return adminLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    /**
     * Lấy log với paging
     */
    public Page<AdminLog> getLogs(int page, int size) {
        return adminLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    /**
     * Thống kê số lượng theo action type
     */
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("warn", adminLogRepository.countByActionType("WARN"));
        stats.put("ban", adminLogRepository.countByActionType("BAN"));
        stats.put("unban", adminLogRepository.countByActionType("UNBAN"));
        stats.put("delete", adminLogRepository.countByActionType("DELETE_COMMENT"));
        stats.put("total", adminLogRepository.count());
        return stats;
    }
}
