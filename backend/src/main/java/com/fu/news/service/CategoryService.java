package com.fu.news.service;

import com.fu.news.dto.CategoryResponse;
import com.fu.news.entity.Category;
import com.fu.news.exception.BadRequestException;
import com.fu.news.exception.ResourceNotFoundException;
import com.fu.news.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> getAllCategories() {
        log.debug("Đang truy vấn toàn bộ danh sách Category từ cơ sở dữ liệu");

        List<Category> categoryList = categoryRepository.findAll();

        List<CategoryResponse> responseList = new ArrayList<>();

        for (Category category : categoryList) {
            CategoryResponse response = CategoryResponse.fromEntity(category);
            responseList.add(response);
        }

        return responseList;
    }

    public CategoryResponse getCategoryById(Long id) {

        if (id == null || id <= 0) {
            throw new BadRequestException("ID chuyên mục không hợp lệ: " + id);
        }

        log.debug("Đang tìm Category với ID: {}", id);

        Optional<Category> optionalCategory = categoryRepository.findById(id);

        if (optionalCategory.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy chuyên mục với ID: " + id);
        }

        Category category = optionalCategory.get();
        return CategoryResponse.fromEntity(category);
    }
}

