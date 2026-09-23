package com.fu.news.dto;

import java.time.LocalDateTime;

/**
 * DTO phản hồi trạng thái sức khỏe của Backend API.
 */
public record HealthResponse(
        String status,
        String appName,
        String version,
        LocalDateTime timestamp
) {}
