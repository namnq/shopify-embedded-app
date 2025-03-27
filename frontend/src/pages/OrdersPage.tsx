import React, { useEffect, useState } from 'react';
import {
  Page,
  Layout,
  Card,
  DataTable,
  Banner,
  Spinner,
  Link,
} from '@shopify/polaris';
import { Order } from '../types';
import { orderApi } from '../services/api';
import { useNavigate } from 'react-router-dom';

export const OrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      const response = await orderApi.getOrders();
      setOrders(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load orders. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Page title="Orders">
        <div style={{ textAlign: 'center', padding: '2rem' }}>
          <Spinner accessibilityLabel="Loading orders" size="large" />
        </div>
      </Page>
    );
  }

  const rows = orders.map((order) => [
    <Link key={order.id} onClick={() => navigate(`/orders/${order.id}`)}>
      #{order.shopifyOrderId}
    </Link>,
    new Date(order.createdAt).toLocaleDateString(),
    order.items.reduce((sum, item) => sum + item.quantity, 0),
    `$${order.total.toFixed(2)}`,
  ]);

  return (
    <Page title="Orders">
      {error && (
        <Banner status="critical" onDismiss={() => setError(null)}>
          <p>{error}</p>
        </Banner>
      )}
      <Layout>
        <Layout.Section>
          <Card>
            <DataTable
              columnContentTypes={['text', 'text', 'numeric', 'numeric']}
              headings={['Order ID', 'Date', 'Items', 'Total']}
              rows={rows}
              footerContent={
                orders.length === 0 ? 'No orders found' : undefined
              }
            />
          </Card>
        </Layout.Section>
      </Layout>
    </Page>
  );
};