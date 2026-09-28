package com.fu.news.dto;

import java.time.LocalDateTime;

public record HealthResponse(
        String status,
        String appName,
        String version,
        LocalDateTime timestamp
) {}

