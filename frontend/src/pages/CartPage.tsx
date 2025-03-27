import React, { useEffect, useState } from 'react';
import {
  Page,
  Layout,
  Card,
  Button,
  Banner,
  Text,
  Stack,
  Spinner,
} from '@shopify/polaris';
import { CartItemCard } from '../components/CartItemCard';
import { Cart } from '../types';
import { cartApi, orderApi } from '../services/api';
import { useNavigate } from 'react-router-dom';

export const CartPage: React.FC = () => {
  const [cart, setCart] = useState<Cart | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [processing, setProcessing] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    loadCart();
  }, []);

  const loadCart = async () => {
    try {
      const response = await cartApi.getCart();
      setCart(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load cart. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateQuantity = async (itemId: number, quantity: number) => {
    try {
      await cartApi.updateCartItem(itemId, quantity);
      await loadCart();
    } catch (err) {
      setError('Failed to update item quantity. Please try again.');
    }
  };

  const handleRemoveItem = async (itemId: number) => {
    try {
      await cartApi.removeFromCart(itemId);
      await loadCart();
    } catch (err) {
      setError('Failed to remove item from cart. Please try again.');
    }
  };

  const handleCheckout = async () => {
    if (!cart) return;
    
    setProcessing(true);
    try {
      const response = await orderApi.createOrder(cart.id);
      navigate(`/orders/${response.data.id}`);
    } catch (err) {
      setError('Failed to process checkout. Please try again.');
      setProcessing(false);
    }
  };

  if (loading) {
    return (
      <Page title="Shopping Cart">
        <div style={{ textAlign: 'center', padding: '2rem' }}>
          <Spinner accessibilityLabel="Loading cart" size="large" />
        </div>
      </Page>
    );
  }

  return (
    <Page
      title="Shopping Cart"
      primaryAction={
        cart?.items.length ? (
          <Button
            primary
            onClick={handleCheckout}
            loading={processing}
          >
            Checkout
          </Button>
        ) : undefined
      }
    >
      {error && (
        <Banner status="critical" onDismiss={() => setError(null)}>
          <p>{error}</p>
        </Banner>
      )}
      <Layout>
        <Layout.Section>
          {!cart?.items.length ? (
            <Card sectioned>
              <Stack vertical alignment="center">
                <Text variant="headingMd" as="h2">
                  Your cart is empty
                </Text>
                <Button onClick={() => navigate('/products')}>
                  Continue Shopping
                </Button>
              </Stack>
            </Card>
          ) : (
            <>
              {cart.items.map((item) => (
                <CartItemCard
                  key={item.id}
                  item={item}
                  onUpdateQuantity={handleUpdateQuantity}
                  onRemove={handleRemoveItem}
                />
              ))}
              <Card sectioned>
                <Stack distribution="trailing">
                  <Text variant="headingLg" as="h2">
                    Total: ${cart.total.toFixed(2)}
                  </Text>
                </Stack>
              </Card>
            </>
          )}
        </Layout.Section>
      </Layout>
    </Page>
  );
};