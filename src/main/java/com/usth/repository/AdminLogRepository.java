package com.usth.repository;

import com.usth.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {

    // Lấy log theo admin
    List<AdminLog> findByAdminIdOrderByCreatedAtDesc(Long adminId);

    // Lấy log theo target user
    List<AdminLog> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId);

    // Lấy log theo action type
    List<AdminLog> findByActionTypeOrderByCreatedAtDesc(String actionType);

    // Lấy tất cả log mới nhất (paging)
    Page<AdminLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Lấy 50 log gần đây nhất
    List<AdminLog> findTop50ByOrderByCreatedAtDesc();

    // Đếm theo action type
    long countByActionType(String actionType);
}
