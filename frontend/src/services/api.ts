import axios from 'axios';
import { Product, Cart, Order } from '../types';

const API_BASE_URL = 'http://localhost:50188';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include token
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('shopify_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const productApi = {
  getProducts: () => api.get<Product[]>('/api/products'),
  getProduct: (id: number) => api.get<Product>(`/api/products/${id}`),
};

export const cartApi = {
  getCart: () => api.get<Cart>('/api/cart'),
  addToCart: (productId: number, quantity: number) => 
    api.post('/api/cart/items', { productId, quantity }),
  updateCartItem: (itemId: number, quantity: number) =>
    api.put(`/api/cart/items/${itemId}`, { quantity }),
  removeFromCart: (itemId: number) =>
    api.delete(`/api/cart/items/${itemId}`),
};

export const orderApi = {
  getOrders: () => api.get<Order[]>('/api/orders'),
  getOrder: (id: number) => api.get<Order>(`/api/orders/${id}`),
  createOrder: (cartId: number) => api.post<Order>('/api/orders', { cartId }),
};

export const authApi = {
  authenticate: (code: string, shop: string) =>
    api.post('/oauth/callback', { code, shop }),
};