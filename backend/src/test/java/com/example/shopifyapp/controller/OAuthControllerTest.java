package com.example.shopifyapp.controller;

import com.example.shopifyapp.security.ShopifyHmacValidator;
import com.example.shopifyapp.service.WebhookRegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OAuthControllerTest {

    @Mock
    private ShopifyHmacValidator hmacValidator;

    @Mock
    private WebhookRegistrationService webhookRegistrationService;

    @Mock
    private ShopRepository shopRepository;

    @Mock
    private JwtService jwtService;

    private OAuthController oAuthController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        oAuthController = new OAuthController(hmacValidator, webhookRegistrationService, shopRepository, jwtService);
        
        // Set required properties
        ReflectionTestUtils.setField(oAuthController, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(oAuthController, "apiSecret", "test-api-secret");
        ReflectionTestUtils.setField(oAuthController, "scopes", "read_products,write_products");
        ReflectionTestUtils.setField(oAuthController, "appHost", "https://test-app.com");
    }

    @Test
    void authorize_ValidShop_ReturnsRedirect() {
        // Given
        String shop = "test-shop.myshopify.com";

        // When
        ResponseEntity<Void> response = oAuthController.authorize(shop);

        // Then
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertTrue(response.getHeaders().getLocation().toString()
            .contains("/admin/oauth/authorize"));
    }

    @Test
    void authorize_InvalidShop_ReturnsBadRequest() {
        // Given
        String shop = "invalid-shop-domain";

        // When
        ResponseEntity<Void> response = oAuthController.authorize(shop);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void callback_ValidHmac_ReturnsRedirect() {
        // Given
        String code = "test_code";
        String hmac = "valid_hmac";
        String shop = "test-shop.myshopify.com";
        String timestamp = "1234567890";
        
        when(hmacValidator.validateOAuthHmac(any(Map.class))).thenReturn(true);

        // When
        ResponseEntity<?> response = oAuthController.callback(code, hmac, shop, timestamp, null);

        // Then
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        verify(webhookRegistrationService).registerWebhooks(anyString(), anyString());
    }

    @Test
    void callback_InvalidHmac_ReturnsUnauthorized() {
        // Given
        String code = "test_code";
        String hmac = "invalid_hmac";
        String shop = "test-shop.myshopify.com";
        String timestamp = "1234567890";
        
        when(hmacValidator.validateOAuthHmac(any(Map.class))).thenReturn(false);

        // When
        ResponseEntity<?> response = oAuthController.callback(code, hmac, shop, timestamp, null);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(webhookRegistrationService, never()).registerWebhooks(anyString(), anyString());
    }

    @Test
    void callback_InvalidShop_ReturnsBadRequest() {
        // Given
        String code = "test_code";
        String hmac = "valid_hmac";
        String shop = "invalid-shop";
        String timestamp = "1234567890";
        
        when(hmacValidator.validateOAuthHmac(any(Map.class))).thenReturn(true);

        // When
        ResponseEntity<?> response = oAuthController.callback(code, hmac, shop, timestamp, null);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(webhookRegistrationService, never()).registerWebhooks(anyString(), anyString());
    }
}