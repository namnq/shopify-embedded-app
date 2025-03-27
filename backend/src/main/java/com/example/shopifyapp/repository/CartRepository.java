package com.example.shopifyapp.repository;

import com.example.shopifyapp.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByShopDomain(String shopDomain);
}