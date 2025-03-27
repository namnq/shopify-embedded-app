# Shopify Embedded App

A full-stack Shopify embedded application built with Spring Boot and React.

## Project Structure

```
shopify-embedded-app/
├── backend/               # Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/             # React application
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── hooks/
│   ├── package.json
│   └── vite.config.ts
└── README.md
```

## Prerequisites

- Java 21 (Temurin/OpenJDK 21.0.6 LTS or later)
- Node.js 18+
- npm 9+
- A Shopify Partner account
- A Shopify development store

## Environment Setup

### Backend Configuration

1. Create a Shopify app in your Shopify Partner account
2. Set the following environment variables:
   ```
   SHOPIFY_API_KEY=your_api_key
   SHOPIFY_API_SECRET=your_api_secret
   SHOPIFY_APP_HOST=http://localhost:50188
   JWT_SECRET=your_jwt_secret
   ```

### Frontend Configuration

1. Create a `.env` file in the frontend directory:
   ```
   VITE_API_BASE_URL=http://localhost:50188
   VITE_SHOPIFY_APP_URL=https://apps.shopify.com/your-app
   VITE_APP_HOST=http://localhost:57883
   ```

## Running the Application

### Backend

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend will start on port 50188.

### Frontend

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will start on port 57883.

## Features

- Product Management
  - List products
  - View product details
  - Manage inventory

- Shopping Cart
  - Add/remove items
  - Update quantities
  - Calculate totals

- Order Processing
  - Create orders
  - View order history
  - Order details

- Shopify Integration
  - OAuth authentication
  - Embedded app SDK integration
  - Secure API communication

## Security

- JWT-based authentication
- Shopify request verification
- CORS configuration
- Secure session management

## Development Guidelines

1. Follow standard coding conventions for both Java and TypeScript
2. Write unit tests for new features
3. Use meaningful commit messages
4. Keep dependencies up to date
5. Document API changes

## Deployment

1. Build the frontend:
   ```bash
   cd frontend
   npm run build
   ```

2. Build the backend:
   ```bash
   cd backend
   ./mvnw clean package
   ```

3. Deploy the generated JAR file to your hosting environment

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.