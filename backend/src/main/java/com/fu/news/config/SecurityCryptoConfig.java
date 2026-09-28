package com.fu.news.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cấu hình Bean mã hóa mật khẩu sử dụng thuật toán BCrypt.
 * Thuật toán này sinh muối (salt) tự động và trả về chuỗi hash an toàn dạng $2a$10$...
 */
@Configuration
public class SecurityCryptoConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
