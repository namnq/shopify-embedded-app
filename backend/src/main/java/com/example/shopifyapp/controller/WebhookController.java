package com.example.shopifyapp.controller;

import com.example.shopifyapp.security.ShopifyHmacValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    private final ShopifyHmacValidator hmacValidator;

    @Autowired
    public WebhookController(ShopifyHmacValidator hmacValidator) {
        this.hmacValidator = hmacValidator;
    }

    @PostMapping("/products/create")
    public ResponseEntity<Void> handleProductCreate(
            @RequestHeader("X-Shopify-Hmac-SHA256") String hmac,
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Topic") String topic,
            HttpServletRequest request
    ) {
        String requestBody = getRequestBody(request);
        
        if (!hmacValidator.validateWebhookHmac(hmac, requestBody)) {
            logger.warn("Invalid HMAC for webhook from shop: {}, topic: {}", shopDomain, topic);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Received product create webhook from shop: {}", shopDomain);
        // TODO: Process product creation
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products/update")
    public ResponseEntity<Void> handleProductUpdate(
            @RequestHeader("X-Shopify-Hmac-SHA256") String hmac,
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Topic") String topic,
            HttpServletRequest request
    ) {
        String requestBody = getRequestBody(request);
        
        if (!hmacValidator.validateWebhookHmac(hmac, requestBody)) {
            logger.warn("Invalid HMAC for webhook from shop: {}, topic: {}", shopDomain, topic);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Received product update webhook from shop: {}", shopDomain);
        // TODO: Process product update
        return ResponseEntity.ok().build();
    }

    @PostMapping("/products/delete")
    public ResponseEntity<Void> handleProductDelete(
            @RequestHeader("X-Shopify-Hmac-SHA256") String hmac,
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Topic") String topic,
            HttpServletRequest request
    ) {
        String requestBody = getRequestBody(request);
        
        if (!hmacValidator.validateWebhookHmac(hmac, requestBody)) {
            logger.warn("Invalid HMAC for webhook from shop: {}, topic: {}", shopDomain, topic);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Received product delete webhook from shop: {}", shopDomain);
        // TODO: Process product deletion
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/create")
    public ResponseEntity<Void> handleOrderCreate(
            @RequestHeader("X-Shopify-Hmac-SHA256") String hmac,
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Topic") String topic,
            HttpServletRequest request
    ) {
        String requestBody = getRequestBody(request);
        
        if (!hmacValidator.validateWebhookHmac(hmac, requestBody)) {
            logger.warn("Invalid HMAC for webhook from shop: {}, topic: {}", shopDomain, topic);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Received order create webhook from shop: {}", shopDomain);
        // TODO: Process order creation
        return ResponseEntity.ok().build();
    }

    @PostMapping("/app/uninstalled")
    public ResponseEntity<Void> handleAppUninstalled(
            @RequestHeader("X-Shopify-Hmac-SHA256") String hmac,
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Topic") String topic,
            HttpServletRequest request
    ) {
        String requestBody = getRequestBody(request);
        
        if (!hmacValidator.validateWebhookHmac(hmac, requestBody)) {
            logger.warn("Invalid HMAC for webhook from shop: {}, topic: {}", shopDomain, topic);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.info("Received app uninstalled webhook from shop: {}", shopDomain);
        // TODO: Clean up shop data
        return ResponseEntity.ok().build();
    }

    private String getRequestBody(HttpServletRequest request) {
        try {
            BufferedReader reader = request.getReader();
            return reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            logger.error("Error reading webhook request body", e);
            return "";
        }
    }
}