package com.fu.news.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Thực thể Category ánh xạ bảng 'categories' trong SQL Server.
 * Quy ước:
 * - status: 1 = Active, 0 = Inactive
 */
@Entity
@Table(name = "categories")
public class Category {

    public static final int STATUS_INACTIVE = 0;
    public static final int STATUS_ACTIVE = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên chuyên mục không được để trống")
    @Size(max = 100, message = "Tên chuyên mục tối đa 100 ký tự")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Trạng thái không được để trống")
    @Column(name = "status", nullable = false)
    private Integer status = STATUS_ACTIVE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Category() {
    }

    public Category(String name, String description, Integer status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = STATUS_ACTIVE;
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return this.status != null && this.status == STATUS_ACTIVE;
    }
}
