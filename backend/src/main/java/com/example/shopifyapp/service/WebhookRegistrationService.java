package com.example.shopifyapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookRegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookRegistrationService.class);
    private final RestTemplate restTemplate;

    @Value("${shopify.app.host}")
    private String appHost;

    private static final String[] WEBHOOK_TOPICS = {
        "products/create",
        "products/update",
        "products/delete",
        "orders/create",
        "app/uninstalled"
    };

    public WebhookRegistrationService() {
        this.restTemplate = new RestTemplate();
    }

    public void registerWebhooks(String shopDomain, String accessToken) {
        String graphqlUrl = String.format("https://%s/admin/api/2024-01/graphql.json", shopDomain);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Shopify-Access-Token", accessToken);

        for (String topic : WEBHOOK_TOPICS) {
            registerWebhook(graphqlUrl, headers, topic);
        }
    }

    private void registerWebhook(String graphqlUrl, HttpHeaders headers, String topic) {
        String callbackUrl = String.format("%s/webhooks/%s", appHost, topic.replace("/", "/"));
        
        String mutation = String.format("""
            mutation {
              webhookSubscriptionCreate(
                topic: %s
                webhookSubscription: {
                  callbackUrl: "%s"
                  format: JSON
                }
              ) {
                userErrors {
                  field
                  message
                }
                webhookSubscription {
                  id
                }
              }
            }
            """, 
            topic.toUpperCase(),
            callbackUrl
        );

        Map<String, String> body = new HashMap<>();
        body.put("query", mutation);

        try {
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(graphqlUrl, request, String.class);
            logger.info("Registered webhook for topic {} with response: {}", topic, response);
        } catch (Exception e) {
            logger.error("Failed to register webhook for topic {}: {}", topic, e.getMessage());
        }
    }

    public void unregisterWebhooks(String shopDomain, String accessToken) {
        // TODO: Implement webhook unregistration logic
        // This would be useful when manually removing a shop or during testing
    }
}