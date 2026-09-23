package com.fu.news;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Main application class for FUNewsManagementSystem.
 * 
 * Lưu ý Stage 1: Exclude các AutoConfiguration liên quan đến Database/JPA
 * để ứng dụng khởi động độc lập, không báo lỗi khi chưa cấu hình SQL Server.
 * Ở Stage 2 khi cấu hình xong database, có thể bỏ phần exclude này.
 */
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class FUNewsManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(FUNewsManagementSystemApplication.class, args);
    }
}
