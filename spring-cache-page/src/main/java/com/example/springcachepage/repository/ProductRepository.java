package com.example.springcachepage.repository;


import com.example.springcachepage.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
