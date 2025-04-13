# Store API

Simple e-commerce API project for product, category, and order management, developed with Java, Spring Boot, Maven, and MySQL.

## Technologies Used
- Java 17
- Spring Boot 3
- Maven
- MySQL
- Docker (optional)

## How to Run
Instructions will be added soon.

## API Security
The application uses JWT-based authentication to secure all endpoints.
The endpoint /auth/login allows users to authenticate and retrieve a valid JWT token.
The token must be included in the Authorization header for all protected requests: Authorization: Bearer {your-token}

- Public (unprotected) endpoints:
  - POST /auth/login
  - POST /mock-payment/stripe
  - POST /mock-payment/paypal
  - 
-Protected endpoints (authentication required):
  -All other endpoints, such as /categories, /products, /orders, etc.
