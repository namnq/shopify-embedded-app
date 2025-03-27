package com.example.shopifyapp.controller;

import com.example.shopifyapp.security.ShopifyHmacValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class WebhookControllerTest {

    @Mock
    private ShopifyHmacValidator hmacValidator;

    private WebhookController webhookController;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webhookController = new WebhookController(hmacValidator);
        request = new MockHttpServletRequest();
    }

    @Test
    void handleProductCreate_ValidHmac_ReturnsOk() {
        // Given
        String hmac = "valid_hmac";
        String shopDomain = "test-shop.myshopify.com";
        String topic = "products/create";
        when(hmacValidator.validateWebhookHmac(anyString(), anyString())).thenReturn(true);

        // When
        ResponseEntity<Void> response = webhookController.handleProductCreate(hmac, shopDomain, topic, request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void handleProductCreate_InvalidHmac_ReturnsUnauthorized() {
        // Given
        String hmac = "invalid_hmac";
        String shopDomain = "test-shop.myshopify.com";
        String topic = "products/create";
        when(hmacValidator.validateWebhookHmac(anyString(), anyString())).thenReturn(false);

        // When
        ResponseEntity<Void> response = webhookController.handleProductCreate(hmac, shopDomain, topic, request);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void handleOrderCreate_ValidHmac_ReturnsOk() {
        // Given
        String hmac = "valid_hmac";
        String shopDomain = "test-shop.myshopify.com";
        String topic = "orders/create";
        when(hmacValidator.validateWebhookHmac(anyString(), anyString())).thenReturn(true);

        // When
        ResponseEntity<Void> response = webhookController.handleOrderCreate(hmac, shopDomain, topic, request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void handleAppUninstalled_ValidHmac_ReturnsOk() {
        // Given
        String hmac = "valid_hmac";
        String shopDomain = "test-shop.myshopify.com";
        String topic = "app/uninstalled";
        when(hmacValidator.validateWebhookHmac(anyString(), anyString())).thenReturn(true);

        // When
        ResponseEntity<Void> response = webhookController.handleAppUninstalled(hmac, shopDomain, topic, request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}