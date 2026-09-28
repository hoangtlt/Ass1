package com.fu.news.model;

import com.fu.news.dto.CategoryRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("CategoryRequest hợp lệ: Không có vi phạm validation nào")
    void validCategoryRequest_ShouldHaveNoViolations() {
        CategoryRequest request = new CategoryRequest(
                "Công nghệ & Đổi mới",
                "Mô tả chuyên mục công nghệ thông tin",
                1
        );

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("CategoryRequest vi phạm: Tên chuyên mục để trống hoặc null")
    void blankName_ShouldViolateNotBlank() {
        CategoryRequest requestEmpty = new CategoryRequest("", "Mô tả", 1);
        CategoryRequest requestNull = new CategoryRequest(null, "Mô tả", 1);
        CategoryRequest requestSpaces = new CategoryRequest("   ", "Mô tả", 1);

        assertThat(validator.validate(requestEmpty))
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertThat(validator.validate(requestNull))
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        assertThat(validator.validate(requestSpaces))
                .anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    @DisplayName("CategoryRequest vi phạm: Tên vượt quá 100 ký tự")
    void longName_ShouldViolateSize() {
        String longName = "A".repeat(101);
        CategoryRequest request = new CategoryRequest(longName, "Mô tả", 1);

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("name") && v.getMessage().contains("100"));
    }

    @Test
    @DisplayName("CategoryRequest vi phạm: Mô tả vượt quá 500 ký tự")
    void longDescription_ShouldViolateSize() {
        String longDesc = "D".repeat(501);
        CategoryRequest request = new CategoryRequest("Hợp lệ", longDesc, 1);

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("description") && v.getMessage().contains("500"));
    }

    @Test
    @DisplayName("CategoryRequest vi phạm: Status là null")
    void nullStatus_ShouldViolateNotNull() {
        CategoryRequest request = new CategoryRequest("Tên hợp lệ", "Mô tả", null);

        Set<ConstraintViolation<CategoryRequest>> violations = validator.validate(request);
        assertThat(violations)
                .anyMatch(v -> v.getPropertyPath().toString().equals("status"));
    }

    @Test
    @DisplayName("CategoryRequest vi phạm: Status khác 0 và 1 (ví dụ: -1 hoặc 2)")
    void invalidStatusValue_ShouldViolateMinMax() {
        CategoryRequest requestNegative = new CategoryRequest("Tên", "Mô tả", -1);
        CategoryRequest requestTooLarge = new CategoryRequest("Tên", "Mô tả", 2);

        assertThat(validator.validate(requestNegative))
                .anyMatch(v -> v.getPropertyPath().toString().equals("status"));
        assertThat(validator.validate(requestTooLarge))
                .anyMatch(v -> v.getPropertyPath().toString().equals("status"));
    }
}

