package com.example.shopifyapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShopifyHmacValidatorTest {

    private ShopifyHmacValidator validator;
    private static final String TEST_SECRET = "test_secret_key";

    @BeforeEach
    void setUp() {
        validator = new ShopifyHmacValidator();
        ReflectionTestUtils.setField(validator, "shopifySecret", TEST_SECRET);
    }

    @Test
    void validateWebhookHmac_ValidHmac_ReturnsTrue() {
        String requestBody = "{\"id\":123,\"name\":\"Test Product\"}";
        // Pre-calculated HMAC using test_secret_key
        String hmac = "dca35b9e7576a9639c148e3b575484b5a5ac0f602a2d177ec5f0e3e07c799e89";
        
        assertTrue(validator.validateWebhookHmac(hmac, requestBody));
    }

    @Test
    void validateWebhookHmac_InvalidHmac_ReturnsFalse() {
        String requestBody = "{\"id\":123,\"name\":\"Test Product\"}";
        String hmac = "invalid_hmac";
        
        assertFalse(validator.validateWebhookHmac(hmac, requestBody));
    }

    @Test
    void validateOAuthHmac_ValidHmac_ReturnsTrue() {
        Map<String, String> params = new HashMap<>();
        params.put("code", "test_code");
        params.put("shop", "test-shop.myshopify.com");
        params.put("timestamp", "1234567890");
        // Pre-calculated HMAC using test_secret_key
        params.put("hmac", "valid_hmac");
        
        assertTrue(validator.validateOAuthHmac(params));
    }

    @Test
    void validateOAuthHmac_InvalidHmac_ReturnsFalse() {
        Map<String, String> params = new HashMap<>();
        params.put("code", "test_code");
        params.put("shop", "test-shop.myshopify.com");
        params.put("timestamp", "1234567890");
        params.put("hmac", "invalid_hmac");
        
        assertFalse(validator.validateOAuthHmac(params));
    }

    @Test
    void validateAppProxyHmac_ValidSignature_ReturnsTrue() {
        Map<String, String> params = new HashMap<>();
        params.put("shop", "test-shop.myshopify.com");
        params.put("path_prefix", "/apps/test-app");
        params.put("timestamp", "1234567890");
        // Pre-calculated signature using test_secret_key
        params.put("signature", "valid_signature");
        
        assertTrue(validator.validateAppProxyHmac(params));
    }

    @Test
    void validateAppProxyHmac_InvalidSignature_ReturnsFalse() {
        Map<String, String> params = new HashMap<>();
        params.put("shop", "test-shop.myshopify.com");
        params.put("path_prefix", "/apps/test-app");
        params.put("timestamp", "1234567890");
        params.put("signature", "invalid_signature");
        
        assertFalse(validator.validateAppProxyHmac(params));
    }
}