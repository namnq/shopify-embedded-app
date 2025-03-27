package com.example.shopifyapp.repository;

import com.example.shopifyapp.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByShopDomainOrderByCreatedAtDesc(String shopDomain);
    Optional<Order> findByShopifyOrderId(String shopifyOrderId);
}