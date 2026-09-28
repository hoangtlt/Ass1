package com.fu.news.model;

import com.fu.news.entity.Category;
import com.fu.news.entity.News;
import com.fu.news.entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DataModelValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Thực thể User: Phải hợp lệ khi cung cấp đầy đủ thông tin chuẩn")
    void validUserShouldPassValidation() {
        User user = new User("Admin", "$2a$10$abcdefg1234567890", User.ROLE_ADMIN, User.STATUS_ACTIVE);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty(), "User hợp lệ không được có lỗi validation");
        assertTrue(user.isAdmin(), "Role 1 phải là Admin");
        assertFalse(user.isStaff(), "Role 1 không phải là Staff");
        assertTrue(user.isActive(), "Status 1 phải là Active");
    }

    @Test
    @DisplayName("Thực thể User: Phải báo lỗi validation khi username hoặc password để trống")
    void userWithBlankFieldsShouldFailValidation() {
        User user = new User("", "", null, null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "User với trường rỗng phải bị validation chặn");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("passwordHash")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("role")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("status")));
    }

    @Test
    @DisplayName("Thực thể Category: Phải hợp lệ khi có tên và trạng thái")
    void validCategoryShouldPassValidation() {
        Category category = new Category("Công nghệ", "Mô tả công nghệ", Category.STATUS_ACTIVE);
        Set<ConstraintViolation<Category>> violations = validator.validate(category);

        assertTrue(violations.isEmpty(), "Category hợp lệ không được có lỗi validation");
        assertEquals("Công nghệ", category.getName());
        assertTrue(category.isActive());
    }

    @Test
    @DisplayName("Thực thể Category: Phải báo lỗi khi tên bị để trống")
    void categoryWithBlankNameShouldFailValidation() {
        Category category = new Category("   ", "Mô tả", Category.STATUS_ACTIVE);
        Set<ConstraintViolation<Category>> violations = validator.validate(category);

        assertFalse(violations.isEmpty(), "Category với tên khoảng trắng phải bị chặn");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Thực thể News: Phải hợp lệ khi liên kết với Category và User hợp lệ")
    void validNewsShouldPassValidation() {
        User author = new User("Admin", "hash123", User.ROLE_ADMIN, User.STATUS_ACTIVE);
        Category category = new Category("Công nghệ", "Mô tả", Category.STATUS_ACTIVE);
        News news = new News("Tiêu đề bài viết", "Nội dung bài viết", category, author, News.STATUS_ACTIVE);

        Set<ConstraintViolation<News>> violations = validator.validate(news);
        assertTrue(violations.isEmpty(), "News hợp lệ không được có lỗi validation");
        assertEquals("Tiêu đề bài viết", news.getTitle());
        assertEquals("Công nghệ", news.getCategory().getName());
        assertEquals("Admin", news.getCreatedBy().getUsername());
        assertTrue(news.isActive());
    }

    @Test
    @DisplayName("Thực thể News: Phải báo lỗi khi thiếu Category hoặc thiếu User tạo bài")
    void newsWithoutCategoryOrUserShouldFailValidation() {
        News news = new News("Tiêu đề", "Nội dung", null, null, News.STATUS_ACTIVE);
        Set<ConstraintViolation<News>> violations = validator.validate(news);

        assertFalse(violations.isEmpty(), "News thiếu Category hoặc User phải bị chặn");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("category")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("createdBy")));
    }
}

