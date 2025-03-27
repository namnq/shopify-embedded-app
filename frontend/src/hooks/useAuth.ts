import { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { authApi } from '../services/api';

export const useAuth = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const token = sessionStorage.getItem('shopify_token');
    if (token) {
      setIsAuthenticated(true);
    }
    setIsLoading(false);
  }, []);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const code = params.get('code');
    const shop = params.get('shop');

    if (code && shop) {
      handleAuthentication(code, shop);
    }
  }, [location]);

  const handleAuthentication = async (code: string, shop: string) => {
    try {
      const response = await authApi.authenticate(code, shop);
      const { token } = response.data;
      sessionStorage.setItem('shopify_token', token);
      setIsAuthenticated(true);
      navigate('/products');
    } catch (error) {
      console.error('Authentication failed:', error);
      setIsAuthenticated(false);
    }
  };

  const logout = () => {
    sessionStorage.removeItem('shopify_token');
    setIsAuthenticated(false);
    navigate('/');
  };

  return {
    isAuthenticated,
    isLoading,
    logout,
  };
};