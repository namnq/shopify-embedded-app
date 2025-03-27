package com.example.shopifyapp.service;

import com.example.shopifyapp.model.Cart;
import com.example.shopifyapp.model.CartItem;
import com.example.shopifyapp.model.Product;
import com.example.shopifyapp.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;

    @Autowired
    public CartService(CartRepository cartRepository, ProductService productService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    public Cart getCartByShopDomain(String shopDomain) {
        return cartRepository.findByShopDomain(shopDomain)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setShopDomain(shopDomain);
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public Cart addItemToCart(String shopDomain, Long productId, Integer quantity) {
        Cart cart = getCartByShopDomain(shopDomain);
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    cart.getItems().add(newItem);
                    return newItem;
                });

        cartItem.setQuantity(quantity);
        cart.recalculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(String shopDomain, Long itemId) {
        Cart cart = getCartByShopDomain(shopDomain);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cart.recalculateTotal();
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(String shopDomain) {
        Cart cart = getCartByShopDomain(shopDomain);
        cart.getItems().clear();
        cart.recalculateTotal();
        cartRepository.save(cart);
    }
}