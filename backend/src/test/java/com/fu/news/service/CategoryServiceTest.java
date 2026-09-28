package com.fu.news.service;

import com.fu.news.dto.CategoryResponse;
import com.fu.news.entity.Category;
import com.fu.news.exception.BadRequestException;
import com.fu.news.exception.ResourceNotFoundException;
import com.fu.news.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    @DisplayName("getAllCategories: Trả về danh sách CategoryResponse khi có dữ liệu")
    void getAllCategories_WhenDataExists_ShouldReturnList() {
        Category cat1 = new Category("Công nghệ & Đổi mới", "Mô tả công nghệ", Category.STATUS_ACTIVE);
        cat1.setId(1L);
        cat1.setCreatedAt(LocalDateTime.now());

        Category cat2 = new Category("Đời sống Sinh viên", "Mô tả sinh viên", Category.STATUS_ACTIVE);
        cat2.setId(2L);
        cat2.setCreatedAt(LocalDateTime.now());

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Công nghệ & Đổi mới");
        assertThat(result.get(1).getName()).isEqualTo("Đời sống Sinh viên");
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllCategories: Trả về danh sách rỗng khi bảng chưa có bản ghi nào")
    void getAllCategories_WhenTableEmpty_ShouldReturnEmptyList() {
        when(categoryRepository.findAll()).thenReturn(Collections.emptyList());

        List<CategoryResponse> result = categoryService.getAllCategories();

        assertThat(result).isNotNull().isEmpty();
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getCategoryById: Trả về CategoryResponse khi tìm thấy ID hợp lệ")
    void getCategoryById_WhenFound_ShouldReturnCategoryResponse() {
        Category cat = new Category("Thông báo Học vụ", "Lịch thi và học bổng", Category.STATUS_ACTIVE);
        cat.setId(3L);
        cat.setCreatedAt(LocalDateTime.now());

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(cat));

        CategoryResponse response = categoryService.getCategoryById(3L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(3L);
        assertThat(response.getName()).isEqualTo("Thông báo Học vụ");
        assertThat(response.getStatus()).isEqualTo(Category.STATUS_ACTIVE);
        verify(categoryRepository, times(1)).findById(3L);
    }

    @Test
    @DisplayName("getCategoryById: Ném ResourceNotFoundException khi ID không tồn tại trong database")
    void getCategoryById_WhenNotFound_ShouldThrowResourceNotFoundException() {
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getCategoryById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");

        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("getCategoryById: Ném BadRequestException khi ID <= 0 hoặc null")
    void getCategoryById_WhenInvalidId_ShouldThrowBadRequestException() {
        assertThatThrownBy(() -> categoryService.getCategoryById(0L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("không hợp lệ");

        assertThatThrownBy(() -> categoryService.getCategoryById(-5L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("không hợp lệ");

        assertThatThrownBy(() -> categoryService.getCategoryById(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("không hợp lệ");

        verifyNoInteractions(categoryRepository);
    }
}

