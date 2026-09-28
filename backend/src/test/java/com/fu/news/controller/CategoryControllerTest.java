package com.fu.news.controller;

import com.fu.news.config.WebConfig;
import com.fu.news.dto.CategoryResponse;
import com.fu.news.exception.BadRequestException;
import com.fu.news.exception.GlobalExceptionHandler;
import com.fu.news.exception.ResourceNotFoundException;
import com.fu.news.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@Import({WebConfig.class, GlobalExceptionHandler.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    @DisplayName("GET /api/categories: Trả về HTTP 200 và danh sách chuyên mục dạng JSON")
    void getAllCategories_WhenDataExists_ShouldReturn200AndJsonList() throws Exception {
        CategoryResponse cat1 = new CategoryResponse(1L, "Công nghệ & Đổi mới", "Mô tả công nghệ", 1, LocalDateTime.now());
        CategoryResponse cat2 = new CategoryResponse(2L, "Đời sống Sinh viên", "Mô tả sinh viên", 1, LocalDateTime.now());

        when(categoryService.getAllCategories()).thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/api/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Công nghệ & Đổi mới"))
                .andExpect(jsonPath("$[0].status").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Đời sống Sinh viên"));
    }

    @Test
    @DisplayName("GET /api/categories: Trả về HTTP 200 và mảng rỗng [] khi chưa có dữ liệu")
    void getAllCategories_WhenEmpty_ShouldReturn200AndEmptyArray() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/categories")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/categories/{id}: Trả về HTTP 200 và chi tiết chuyên mục khi ID tồn tại")
    void getCategoryById_WhenFound_ShouldReturn200AndCategory() throws Exception {
        CategoryResponse cat = new CategoryResponse(1L, "Công nghệ & Đổi mới", "Mô tả công nghệ", 1, LocalDateTime.now());

        when(categoryService.getCategoryById(1L)).thenReturn(cat);

        mockMvc.perform(get("/api/categories/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Công nghệ & Đổi mới"))
                .andExpect(jsonPath("$.status").value(1));
    }

    @Test
    @DisplayName("GET /api/categories/{id}: Trả về HTTP 404 với định dạng ErrorResponse khi ID không tồn tại")
    void getCategoryById_WhenNotFound_ShouldReturn404AndStandardErrorFormat() throws Exception {
        when(categoryService.getCategoryById(999L))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy chuyên mục với ID: 999"));

        mockMvc.perform(get("/api/categories/999")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Không tìm thấy chuyên mục với ID: 999"))
                .andExpect(jsonPath("$.path").value("/api/categories/999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/categories/{id}: Trả về HTTP 400 khi ID là số âm hoặc 0 (BadRequestException)")
    void getCategoryById_WhenInvalidIdValue_ShouldReturn400AndStandardErrorFormat() throws Exception {
        when(categoryService.getCategoryById(-1L))
                .thenThrow(new BadRequestException("ID chuyên mục không hợp lệ: -1"));

        mockMvc.perform(get("/api/categories/-1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("ID chuyên mục không hợp lệ: -1"))
                .andExpect(jsonPath("$.path").value("/api/categories/-1"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/categories/abc: Trả về HTTP 400 khi ID sai kiểu dữ liệu (TypeMismatch)")
    void getCategoryById_WhenTypeMismatch_ShouldReturn400AndStandardErrorFormat() throws Exception {
        mockMvc.perform(get("/api/categories/abc")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/categories/abc"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}

