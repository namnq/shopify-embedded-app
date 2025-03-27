package com.example.shopifyapp.repository;

import com.example.shopifyapp.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    Optional<Shop> findByShopDomain(String shopDomain);
    
    boolean existsByShopDomain(String shopDomain);
    
    @Modifying
    @Query("UPDATE Shop s SET s.isActive = false WHERE s.shopDomain = ?1")
    void deactivateByShopDomain(String shopDomain);
    
    @Query("SELECT s.accessToken FROM Shop s WHERE s.shopDomain = ?1 AND s.isActive = true")
    Optional<String> findAccessTokenByShopDomain(String shopDomain);
}