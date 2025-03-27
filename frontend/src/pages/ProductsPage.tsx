import React, { useEffect, useState } from 'react';
import { Page, Layout, Grid, Banner, Spinner } from '@shopify/polaris';
import { ProductCard } from '../components/ProductCard';
import { Product } from '../types';
import { productApi, cartApi } from '../services/api';
import { useNavigate } from 'react-router-dom';

export const ProductsPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const response = await productApi.getProducts();
      setProducts(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load products. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (productId: number, quantity: number) => {
    try {
      await cartApi.addToCart(productId, quantity);
      navigate('/cart');
    } catch (err) {
      setError('Failed to add item to cart. Please try again.');
    }
  };

  if (loading) {
    return (
      <Page title="Products">
        <div style={{ textAlign: 'center', padding: '2rem' }}>
          <Spinner accessibilityLabel="Loading products" size="large" />
        </div>
      </Page>
    );
  }

  return (
    <Page title="Products">
      {error && (
        <Banner status="critical" onDismiss={() => setError(null)}>
          <p>{error}</p>
        </Banner>
      )}
      <Layout>
        <Layout.Section>
          <Grid>
            {products.map((product) => (
              <Grid.Cell key={product.id} columnSpan={{ xs: 6, sm: 4, md: 3, lg: 3 }}>
                <ProductCard
                  product={product}
                  onAddToCart={handleAddToCart}
                />
              </Grid.Cell>
            ))}
          </Grid>
        </Layout.Section>
      </Layout>
    </Page>
  );
};