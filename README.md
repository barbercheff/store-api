# Store API

## Table of Contents
- [About the Project](#about-the-project)
- [Technologies Used](#technologies-used)
- [How to Run](#how-to-run)
- [API Endpoints](#api-endpoints)
- [API Security](#api-security)
- [Error Handling](#error-handling)

## About the Project
Simple e-commerce API project for product, category, and order management, developed with Java, Spring Boot, Maven, and MySQL.

## Technologies Used
- Java 17
- Spring Boot 3
- Maven
- MySQL
- Docker (optional)

## How to Run
1. Clone the project
2. Set up the database (see `init.sql` if available).
3. Configure your `application.properties`.
4. Run the project:
   ```bash
   ./mvn spring-boot:run
   ```
5. The API will be available at `http://localhost:8080/`

## API Endpoints

### Categories
- `GET /categories`
- `GET /categories/{id}`
- `POST /categories`
- `PUT /categories/{id}`
- `DELETE /categories/{id}`

### Products
- `GET /products`
- `GET /products/{id}`
- `POST /products`
- `PUT /products/{id}`
- `DELETE /products/{id}`

### Orders
- `GET /orders`
- `GET /orders/{id}`
- `POST /orders`
- `PUT /orders/{id}`
- `DELETE /orders/{id}`
- `POST /orders/{id}/finish`
- `POST /orders/{id}/cancel`

### Mock Payment Gateway
- `POST /mock-payment/stripe`
- `POST /mock-payment/paypal`

## API Security
The application uses JWT-based authentication to secure all endpoints.  
The endpoint `/auth/login` allows users to authenticate and retrieve a valid JWT token.  
The token must be included in the Authorization header for all protected requests:  
`Authorization: Bearer {your-token}`

### Public (unprotected) endpoints:
- POST `/auth/login`
- POST `/mock-payment/stripe`
- POST `/mock-payment/paypal`

### Protected endpoints (authentication required):
- All other endpoints, such as `/categories`, `/products`, `/orders`, etc.

### Authentication Flow
1. The user sends their credentials (username and password) to `/auth/login`.
2. If the credentials are correct, a JWT token valid for 24 hours is returned.
3. This token must be included in the Authorization header of subsequent API requests.
4. If the token is missing, invalid, or expired, the API will respond with a 401 Unauthorized error.

### Authentication Details
- JWT Secret: Defined in `application.properties`.
- Token expiration time: 24 hours (configurable).
- Login credentials:
    - Username: defined in `application.properties` (`auth.username`)
    - Password: defined in `application.properties` (`auth.password`)

### How to authenticate using Postman
1. **Login request**:
    - Method: POST
    - URL: `http://localhost:8080/auth/login`
    - Params:
        - username (form-data or query param)
        - password (form-data or query param)

2. **Copy the token** received in the response.

3. **Authorized requests**:  
   For any protected endpoint, add a header with:
    - Key: `Authorization`
    - Value: `Bearer {your-token}`

### Notes
The authentication system is based on a static username and password for simplicity, without a user database.

Possible future improvements could include:
- Adding a user registration system
- Storing users and passwords securely in a database
- Refresh tokens mechanism

## Error Handling

The application uses a **global exception handler** (`@RestControllerAdvice`) to handle and format errors consistently across the API.

### Error Response Format
All error responses have the following structure:

```json
{
  "error": "Error description",
  "status": HTTP status code,
  "timestamp": "Date and time of the error"
}
```

### Handled Exceptions

| Exception | HTTP Status | Description |
|:----------|:------------|:------------|
| `ResourceNotFoundException` | 404 Not Found | Resource not found |
| `OutOfStockException` | 400 Bad Request | Product has no stock available |
| `OrderAlreadyFinishedException` | 409 Conflict | Trying to modify an already finished order |
| `PaymentGatewayException` | 502 Bad Gateway | Error calling external payment service |
| `PaymentStatusNullException` | 502 Bad Gateway | Payment gateway returned null |
| `UnsupportedPaymentGatewayException` | 400 Bad Request | Unsupported payment gateway type |
| `InvalidCategoryHierarchyException` | 400 Bad Request | Circular parent-child relationship in categories |
| `CategoryDeletionException` | 409 Conflict | Attempting to delete a category that still has child categories |
| `CategoryAlreadyExistsException` | 409 Conflict | Duplicate category name |
| `MethodArgumentNotValidException` | 400 Bad Request | Validation errors in request body |
| `Exception` (generic) | 500 Internal Server Error | Unhandled exception |