package com.fu.news.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashingTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("Mật khẩu Admin phải được mã hóa BCrypt, khác hoàn toàn chuỗi thô và có tiền tố $2a$")
    void adminPasswordShouldBeHashedWithBcrypt() {
        String rawPassword = "Admin";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(hashedPassword, "Chuỗi hash không được null");
        assertNotEquals(rawPassword, hashedPassword, "Mật khẩu tuyệt đối không được lưu chuỗi thô 'Admin'");
        assertTrue(hashedPassword.startsWith("$2a$") || hashedPassword.startsWith("$2b$"),
                "Mật khẩu hash phải tuân theo định dạng BCrypt (bắt đầu bằng $2a$ hoặc $2b$)");
    }

    @Test
    @DisplayName("Kiểm tra khớp mật khẩu Admin khi xác thực và từ chối mật khẩu sai")
    void passwordMatchesShouldWorkCorrectly() {
        String rawPassword = "Admin";
        String hashedPassword = passwordEncoder.encode(rawPassword);

        assertTrue(passwordEncoder.matches("Admin", hashedPassword),
                "Xác thực mật khẩu đúng 'Admin' phải thành công");
        assertFalse(passwordEncoder.matches("admin", hashedPassword),
                "Mật khẩu phân biệt chữ hoa/thường: 'admin' phải bị từ chối");
        assertFalse(passwordEncoder.matches("WrongPassword", hashedPassword),
                "Mật khẩu sai phải bị từ chối");
        assertFalse(passwordEncoder.matches("", hashedPassword),
                "Mật khẩu rỗng phải bị từ chối");
    }
}

