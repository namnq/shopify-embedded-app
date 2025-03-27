import React from 'react';
import { Frame, Navigation, TopBar } from '@shopify/polaris';
import { 
  HomeMinor, 
  ProductsMinor, 
  CartMajor, 
  OrdersMajor 
} from '@shopify/polaris-icons';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

interface AppLayoutProps {
  children: React.ReactNode;
}

export const AppLayout: React.FC<AppLayoutProps> = ({ children }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAuthenticated, logout } = useAuth();

  const navigationMarkup = (
    <Navigation location={location.pathname}>
      <Navigation.Section
        items={[
          {
            label: 'Home',
            icon: HomeMinor,
            onClick: () => navigate('/'),
            selected: location.pathname === '/',
          },
          {
            label: 'Products',
            icon: ProductsMinor,
            onClick: () => navigate('/products'),
            selected: location.pathname === '/products',
          },
          {
            label: 'Cart',
            icon: CartMajor,
            onClick: () => navigate('/cart'),
            selected: location.pathname === '/cart',
          },
          {
            label: 'Orders',
            icon: OrdersMajor,
            onClick: () => navigate('/orders'),
            selected: location.pathname === '/orders',
          },
        ]}
      />
    </Navigation>
  );

  const topBarMarkup = (
    <TopBar
      showNavigationToggle
      userMenu={
        isAuthenticated ? (
          <TopBar.UserMenu
            actions={[
              {
                items: [{ content: 'Logout', onAction: logout }],
              },
            ]}
            name="Store"
            initials="S"
          />
        ) : undefined
      }
    />
  );

  return (
    <Frame
      topBar={topBarMarkup}
      navigation={navigationMarkup}
    >
      {children}
    </Frame>
  );
};