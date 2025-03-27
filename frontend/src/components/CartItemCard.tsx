import React from 'react';
import {
  Card,
  Button,
  Text,
  Stack,
  ButtonGroup,
  TextField,
} from '@shopify/polaris';
import { CartItem } from '../types';

interface CartItemCardProps {
  item: CartItem;
  onUpdateQuantity: (itemId: number, quantity: number) => void;
  onRemove: (itemId: number) => void;
}

export const CartItemCard: React.FC<CartItemCardProps> = ({
  item,
  onUpdateQuantity,
  onRemove,
}) => {
  const handleQuantityChange = (value: string) => {
    const quantity = parseInt(value, 10);
    if (!isNaN(quantity) && quantity > 0) {
      onUpdateQuantity(item.id, quantity);
    }
  };

  return (
    <Card sectioned>
      <Stack alignment="center">
        <Stack.Item fill>
          <Text variant="headingMd" as="h3">
            {item.product.title}
          </Text>
        </Stack.Item>
        <Stack.Item>
          <Stack alignment="center" spacing="tight">
            <TextField
              label="Quantity"
              type="number"
              value={item.quantity.toString()}
              onChange={handleQuantityChange}
              min={1}
              autoComplete="off"
            />
            <ButtonGroup>
              <Button onClick={() => onRemove(item.id)} destructive>
                Remove
              </Button>
            </ButtonGroup>
          </Stack>
        </Stack.Item>
        <Stack.Item>
          <Text variant="headingMd" as="p">
            ${(item.product.price * item.quantity).toFixed(2)}
          </Text>
        </Stack.Item>
      </Stack>
    </Card>
  );
};