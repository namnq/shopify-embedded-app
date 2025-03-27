import React from 'react';
import {
  Card,
  Button,
  Text,
  Stack,
  Thumbnail,
  TextContainer,
} from '@shopify/polaris';
import { Product } from '../types';

interface ProductCardProps {
  product: Product;
  onAddToCart: (productId: number, quantity: number) => void;
}

export const ProductCard: React.FC<ProductCardProps> = ({ product, onAddToCart }) => {
  return (
    <Card sectioned>
      <Stack vertical>
        <Thumbnail
          source={product.imageUrl || 'https://via.placeholder.com/150'}
          alt={product.title}
        />
        <TextContainer spacing="tight">
          <Text variant="headingMd" as="h2">
            {product.title}
          </Text>
          <Text variant="bodyMd" as="p" color="subdued">
            {product.description}
          </Text>
          <Text variant="headingLg" as="p">
            ${product.price.toFixed(2)}
          </Text>
          {product.inventory > 0 ? (
            <Button
              primary
              onClick={() => onAddToCart(product.id, 1)}
            >
              Add to Cart
            </Button>
          ) : (
            <Button disabled>Out of Stock</Button>
          )}
        </TextContainer>
      </Stack>
    </Card>
  );
};