package com.fu.news.repository;

import com.fu.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByCategoryId(Long categoryId);

    boolean existsByCategoryId(Long categoryId);

    List<News> findByCreatedById(Long userId);

    boolean existsByCreatedById(Long userId);

    List<News> findByStatus(Integer status);

    List<News> findByTitleContainingIgnoreCase(String keyword);

    boolean existsByTitle(String title);
}

