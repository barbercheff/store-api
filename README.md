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

### Public (unprotected) endpoints:
  - POST /auth/login
  - POST /mock-payment/stripe
  - POST /mock-payment/paypal
     
### Protected endpoints (authentication required):
  - All other endpoints, such as /categories, /products, /orders, etc.
 
### Authentication Flow
  1. The user sends their credentials (username and password) to /auth/login.

  2. If the credentials are correct, a JWT token valid for 24 hours is returned.

  3. This token must be included in the Authorization header of subsequent API requests.

  4. If the token is missing, invalid, or expired, the API will respond with a 401 Unauthorized error.
 
### Authentication Details
  - JWT Secret: Defined in application.properties.
  - Token expiration time: 24 hours (configurable).
  - Login credentials:
    - Username: defined in application.properties (auth.username)
    - Password: defined in application.properties (auth.password)

### How to authenticate using Postman
  1. Login request:
     - Method: POST
     - URL: http://localhost:8080/auth/login
     - Params:
       - username (form-data or query param)
       - password (form-data or query param)

  2. Copy the token received in the response.

  3. Authorized requests:
     For any protected endpoint, add a header with:
       - Key: Authorization
       - Value: Bearer {your-token}

### Notes
  The authentication system is based on a static username and password for simplicity, without a user database.

  Possible future improvements could include:
  - Adding a user registration system
  - Storing users and passwords securely in a database
  - Refresh tokens mechanism

