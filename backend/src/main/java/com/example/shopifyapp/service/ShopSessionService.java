package com.example.shopifyapp.service;

import com.example.shopifyapp.model.Shop;
import com.example.shopifyapp.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ShopSessionService {

    private final ShopRepository shopRepository;

    @Autowired
    public ShopSessionService(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Transactional
    public Shop createOrUpdateShopSession(String shopDomain, String accessToken) {
        Optional<Shop> existingShop = shopRepository.findByShopDomain(shopDomain);
        
        Shop shop = existingShop.orElse(new Shop());
        shop.setShopDomain(shopDomain);
        shop.setAccessToken(accessToken);
        shop.setActive(true);
        
        return shopRepository.save(shop);
    }

    @Transactional(readOnly = true)
    public Optional<Shop> getActiveShopSession(String shopDomain) {
        return shopRepository.findByShopDomain(shopDomain)
            .filter(Shop::isActive);
    }

    @Transactional(readOnly = true)
    public boolean isValidShopSession(String shopDomain) {
        return shopRepository.findByShopDomain(shopDomain)
            .map(Shop::isActive)
            .orElse(false);
    }

    @Transactional
    public void invalidateShopSession(String shopDomain) {
        shopRepository.deactivateByShopDomain(shopDomain);
    }

    @Transactional(readOnly = true)
    public Optional<String> getAccessToken(String shopDomain) {
        return shopRepository.findAccessTokenByShopDomain(shopDomain);
    }
}