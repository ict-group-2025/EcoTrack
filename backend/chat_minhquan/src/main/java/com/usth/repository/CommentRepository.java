package com.usth.repository;

import com.usth.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Lấy comment theo Location ID
    List<Comment> findByLocationId(Long locationId);
}