package com.example.springsecurity.repository;


import com.example.springsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    // Tìm kiếm người dùng theo email, trả về Optional<User> để xử lý trường hợp không tìm thấy người dùng.
}
