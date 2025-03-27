package com.example.shopifyapp.service;

import com.example.shopifyapp.model.Shop;
import com.example.shopifyapp.repository.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ShopSessionServiceTest {

    @Mock
    private ShopRepository shopRepository;

    private ShopSessionService shopSessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        shopSessionService = new ShopSessionService(shopRepository);
    }

    @Test
    void createOrUpdateShopSession_NewShop_CreatesSession() {
        // Given
        String shopDomain = "test-shop.myshopify.com";
        String accessToken = "test-token";
        
        when(shopRepository.findByShopDomain(shopDomain)).thenReturn(Optional.empty());
        when(shopRepository.save(any(Shop.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Shop result = shopSessionService.createOrUpdateShopSession(shopDomain, accessToken);

        // Then
        assertNotNull(result);
        assertEquals(shopDomain, result.getShopDomain());
        assertEquals(accessToken, result.getAccessToken());
        assertTrue(result.isActive());
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    void createOrUpdateShopSession_ExistingShop_UpdatesSession() {
        // Given
        String shopDomain = "test-shop.myshopify.com";
        String oldToken = "old-token";
        String newToken = "new-token";
        
        Shop existingShop = new Shop();
        existingShop.setShopDomain(shopDomain);
        existingShop.setAccessToken(oldToken);
        existingShop.setActive(true);
        
        when(shopRepository.findByShopDomain(shopDomain)).thenReturn(Optional.of(existingShop));
        when(shopRepository.save(any(Shop.class))).thenAnswer(i -> i.getArgument(0));

        // When
        Shop result = shopSessionService.createOrUpdateShopSession(shopDomain, newToken);

        // Then
        assertNotNull(result);
        assertEquals(shopDomain, result.getShopDomain());
        assertEquals(newToken, result.getAccessToken());
        assertTrue(result.isActive());
        verify(shopRepository).save(any(Shop.class));
    }

    @Test
    void getActiveShopSession_ActiveShop_ReturnsShop() {
        // Given
        String shopDomain = "test-shop.myshopify.com";
        Shop activeShop = new Shop();
        activeShop.setShopDomain(shopDomain);
        activeShop.setActive(true);
        
        when(shopRepository.findByShopDomain(shopDomain)).thenReturn(Optional.of(activeShop));

        // When
        Optional<Shop> result = shopSessionService.getActiveShopSession(shopDomain);

        // Then
        assertTrue(result.isPresent());
        assertEquals(shopDomain, result.get().getShopDomain());
    }

    @Test
    void getActiveShopSession_InactiveShop_ReturnsEmpty() {
        // Given
        String shopDomain = "test-shop.myshopify.com";
        Shop inactiveShop = new Shop();
        inactiveShop.setShopDomain(shopDomain);
        inactiveShop.setActive(false);
        
        when(shopRepository.findByShopDomain(shopDomain)).thenReturn(Optional.of(inactiveShop));

        // When
        Optional<Shop> result = shopSessionService.getActiveShopSession(shopDomain);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void invalidateShopSession_DeactivatesShop() {
        // Given
        String shopDomain = "test-shop.myshopify.com";

        // When
        shopSessionService.invalidateShopSession(shopDomain);

        // Then
        verify(shopRepository).deactivateByShopDomain(shopDomain);
    }
}