export interface Product {
  id: number;
  title: string;
  description: string;
  price: number;
  imageUrl: string;
  shopifyProductId: string;
  inventory: number;
  vendor: string;
  productType: string;
}

export interface CartItem {
  id: number;
  product: Product;
  quantity: number;
}

export interface Cart {
  id: number;
  shopDomain: string;
  items: CartItem[];
  total: number;
}

export interface Order {
  id: number;
  shopifyOrderId: string;
  shopDomain: string;
  items: OrderItem[];
  total: number;
  createdAt: string;
}

export interface OrderItem {
  id: number;
  product: Product;
  quantity: number;
  price: number;
}