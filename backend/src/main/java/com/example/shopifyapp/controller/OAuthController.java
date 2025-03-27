package com.example.shopifyapp.controller;

import com.example.shopifyapp.security.ShopifyHmacValidator;
import com.example.shopifyapp.service.WebhookRegistrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OAuthController {

    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
    private final ShopifyHmacValidator hmacValidator;
    private final WebhookRegistrationService webhookRegistrationService;
    private final RestTemplate restTemplate;

    @Value("${shopify.app.api-key}")
    private String apiKey;

    @Value("${shopify.app.api-secret}")
    private String apiSecret;

    @Value("${shopify.app.scopes}")
    private String scopes;

    @Value("${shopify.app.host}")
    private String appHost;

    @Autowired
    public OAuthController(ShopifyHmacValidator hmacValidator, WebhookRegistrationService webhookRegistrationService) {
        this.hmacValidator = hmacValidator;
        this.webhookRegistrationService = webhookRegistrationService;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/authorize")
    public ResponseEntity<Void> authorize(@RequestParam String shop) {
        if (!isValidShopDomain(shop)) {
            logger.warn("Invalid shop domain: {}", shop);
            return ResponseEntity.badRequest().build();
        }

        String redirectUri = appHost + "/oauth/callback";
        String authUrl = String.format(
            "https://%s/admin/oauth/authorize?client_id=%s&scope=%s&redirect_uri=%s",
            shop, apiKey, scopes, redirectUri
        );

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(java.net.URI.create(authUrl))
            .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(
            @RequestParam String code,
            @RequestParam String hmac,
            @RequestParam String shop,
            @RequestParam String timestamp,
            @RequestParam(required = false) String state
    ) {
        // Create params map for HMAC validation
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("hmac", hmac);
        params.put("shop", shop);
        params.put("timestamp", timestamp);
        if (state != null) {
            params.put("state", state);
        }

        // Validate HMAC
        if (!hmacValidator.validateOAuthHmac(params)) {
            logger.warn("Invalid HMAC in OAuth callback for shop: {}", shop);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Validate shop domain
        if (!isValidShopDomain(shop)) {
            logger.warn("Invalid shop domain in callback: {}", shop);
            return ResponseEntity.badRequest().build();
        }

        try {
            // Exchange code for access token
            String accessToken = exchangeCodeForToken(shop, code);
            
            // Register webhooks
            webhookRegistrationService.registerWebhooks(shop, accessToken);

            // TODO: Store shop and token in database
            
            // Redirect to app home with success
            String redirectUrl = String.format("/app?shop=%s&success=true", shop);
            return ResponseEntity.status(HttpStatus.FOUND)
                .location(java.net.URI.create(redirectUrl))
                .build();

        } catch (Exception e) {
            logger.error("Error during OAuth callback processing for shop: {}", shop, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String exchangeCodeForToken(String shop, String code) {
        String tokenUrl = String.format("https://%s/admin/oauth/access_token", shop);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", apiKey);
        requestBody.put("client_secret", apiSecret);
        requestBody.put("code", code);
        
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(
            tokenUrl, 
            request, 
            Map.class
        );
        
        if (response.getBody() != null && response.getBody().containsKey("access_token")) {
            return response.getBody().get("access_token").toString();
        }
        
        throw new RuntimeException("Failed to obtain access token");
    }

    private boolean isValidShopDomain(String shop) {
        return shop != null &&
               shop.endsWith(".myshopify.com") &&
               !shop.contains("..") &&
               !shop.contains("//");
    }
}