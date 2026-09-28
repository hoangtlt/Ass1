package com.fu.news.repository;

import com.fu.news.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByRole(Integer role);

    List<User> findByStatus(Integer status);

    List<User> findByUsernameContainingIgnoreCase(String keyword);
}

