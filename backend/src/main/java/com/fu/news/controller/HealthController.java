package com.fu.news.controller;

import com.fu.news.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Controller kiểm tra trạng thái hoạt động của hệ thống.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> getHealth() {
        HealthResponse response = new HealthResponse(
                "UP",
                "FUNewsManagementSystem",
                "1.0.0-STAGE1",
                LocalDateTime.now()
        );
        return ResponseEntity.ok(response);
    }
}
