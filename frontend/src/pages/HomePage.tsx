import React from 'react';
import {
  Page,
  Layout,
  Card,
  Button,
  Text,
  Stack,
  Link,
} from '@shopify/polaris';
import { useAuth } from '../hooks/useAuth';

export const HomePage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const shopifyAppUrl = import.meta.env.VITE_SHOPIFY_APP_URL || 'https://apps.shopify.com/your-app';

  return (
    <Page title="Welcome to Your Shopify App">
      <Layout>
        <Layout.Section>
          <Card sectioned>
            <Stack vertical spacing="loose">
              <Text variant="headingLg" as="h2">
                Get Started with Your Custom Shopify Experience
              </Text>
              <Text variant="bodyMd" as="p">
                This app helps you manage your products, handle orders, and provide a seamless shopping experience for your customers.
              </Text>
              {!isAuthenticated && (
                <div style={{ marginTop: '1rem' }}>
                  <Button
                    primary
                    external
                    url={shopifyAppUrl}
                  >
                    Install App
                  </Button>
                </div>
              )}
            </Stack>
          </Card>
        </Layout.Section>

        <Layout.Section secondary>
          <Card sectioned>
            <Stack vertical spacing="tight">
              <Text variant="headingMd" as="h3">
                Quick Links
              </Text>
              <Link url="/products">Browse Products</Link>
              <Link url="/cart">View Cart</Link>
              <Link url="/orders">Check Orders</Link>
            </Stack>
          </Card>

          <Card sectioned>
            <Stack vertical spacing="tight">
              <Text variant="headingMd" as="h3">
                Need Help?
              </Text>
              <Text variant="bodyMd" as="p">
                Check out our documentation or contact support for assistance.
              </Text>
              <Button external url="https://help.shopify.com">
                View Documentation
              </Button>
            </Stack>
          </Card>
        </Layout.Section>
      </Layout>
    </Page>
  );
};