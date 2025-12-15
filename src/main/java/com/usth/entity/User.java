package com.usth.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
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

    // Một User có thể comment nhiều lần
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore // Tránh vòng lặp vô tận khi xuất JSON
    private List<Comment> comments;
}