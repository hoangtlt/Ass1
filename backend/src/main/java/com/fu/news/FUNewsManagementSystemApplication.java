package com.fu.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for FUNewsManagementSystem.
 * Ở Stage 2, Spring Data JPA và DataSource AutoConfiguration được kích hoạt
 * để kết nối đến Microsoft SQL Server và quản lý dữ liệu.
 */
@SpringBootApplication
public class FUNewsManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FUNewsManagementSystemApplication.class, args);
    }
}
