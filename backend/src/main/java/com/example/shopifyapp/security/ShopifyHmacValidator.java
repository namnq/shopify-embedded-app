package com.example.shopifyapp.security;

import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ShopifyHmacValidator {

    @Value("${shopify.app.api-secret}")
    private String shopifySecret;

    /**
     * Validates the HMAC from Shopify webhook requests
     * @param hmac The HMAC signature from the request header
     * @param requestBody The raw request body
     * @return true if the HMAC is valid
     */
    public boolean validateWebhookHmac(String hmac, String requestBody) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(shopifySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(requestBody.getBytes(StandardCharsets.UTF_8));
            String calculatedHmac = Hex.encodeHexString(hmacBytes);
            return hmac.equals(calculatedHmac);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates the HMAC from Shopify OAuth callback
     * @param params The query parameters from the callback request
     * @return true if the HMAC is valid
     */
    public boolean validateOAuthHmac(Map<String, String> params) {
        try {
            String hmac = params.get("hmac");
            if (hmac == null) {
                return false;
            }

            // Sort parameters alphabetically, excluding hmac
            TreeMap<String, String> sortedParams = new TreeMap<>(params);
            sortedParams.remove("hmac");

            // Build query string
            StringBuilder queryString = new StringBuilder();
            sortedParams.forEach((key, value) -> {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(key).append("=").append(value);
            });

            // Calculate HMAC
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(shopifySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(queryString.toString().getBytes(StandardCharsets.UTF_8));
            String calculatedHmac = Hex.encodeHexString(hmacBytes);

            return hmac.equals(calculatedHmac);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates the HMAC from Shopify app proxy requests
     * @param params The query parameters from the proxy request
     * @return true if the HMAC is valid
     */
    public boolean validateAppProxyHmac(Map<String, String> params) {
        try {
            String signature = params.get("signature");
            if (signature == null) {
                return false;
            }

            // Sort parameters alphabetically, excluding signature
            TreeMap<String, String> sortedParams = new TreeMap<>(params);
            sortedParams.remove("signature");

            // Build query string
            StringBuilder queryString = new StringBuilder();
            sortedParams.forEach((key, value) -> {
                if (queryString.length() > 0) {
                    queryString.append("&");
                }
                queryString.append(key).append("=").append(value);
            });

            // Calculate HMAC
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(shopifySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] hmacBytes = mac.doFinal(queryString.toString().getBytes(StandardCharsets.UTF_8));
            String calculatedSignature = Hex.encodeHexString(hmacBytes);

            return signature.equals(calculatedSignature);
        } catch (Exception e) {
            return false;
        }
    }
}