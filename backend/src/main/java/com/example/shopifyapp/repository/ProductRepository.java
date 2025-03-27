package com.example.shopifyapp.repository;

import com.example.shopifyapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByShopifyProductId(String shopifyProductId);
}