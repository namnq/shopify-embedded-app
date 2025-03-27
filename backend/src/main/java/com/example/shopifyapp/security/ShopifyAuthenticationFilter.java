package com.example.shopifyapp.security;

import com.example.shopifyapp.service.ShopSessionService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class ShopifyAuthenticationFilter extends OncePerRequestFilter {

    private final ShopSessionService shopSessionService;
    private final ShopifyHmacValidator hmacValidator;
    
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/oauth/authorize",
        "/oauth/callback",
        "/api/webhooks"  // Webhooks are authenticated differently
    );

    @Autowired
    public ShopifyAuthenticationFilter(
            ShopSessionService shopSessionService,
            ShopifyHmacValidator hmacValidator) {
        this.shopSessionService = shopSessionService;
        this.hmacValidator = hmacValidator;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String shop = request.getParameter("shop");
        
        if (shop == null || shop.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing shop parameter");
            return;
        }

        // Validate session
        if (!shopSessionService.isValidShopSession(shop)) {
            response.sendRedirect("/oauth/authorize?shop=" + shop);
            return;
        }

        // For API requests, validate the session token
        if (request.getServletPath().startsWith("/api/")) {
            String sessionToken = request.getHeader("X-Shopify-Session-Token");
            if (sessionToken == null || !hmacValidator.validateSessionToken(sessionToken, shop)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid session token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}