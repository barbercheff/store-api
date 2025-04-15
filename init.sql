CREATE TABLE categories (
    category_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    parent_category_id BIGINT,
    FOREIGN KEY (parent_category_id) REFERENCES categories(category_id)
);

CREATE TABLE products (
    product_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    price DECIMAL(8,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    category_id BIGINT NOT NULL,
    image_url VARCHAR(255),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE orders (
    order_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    total_price DECIMAL(8,2) NOT NULL,
    card_token VARCHAR(255),
    payment_status ENUM('PENDING', 'PAID', 'FAILED','OFFLINE') NOT NULL,
    payment_date DATETIME,
    payment_gateway ENUM('STRIPE', 'PAYPAL'),
    status ENUM('OPEN', 'DROPPED', 'FINISHED') NOT NULL,
    buyer_email VARCHAR(255) NOT NULL,
    seat_letter CHAR(1) NOT NULL,
    seat_number INT NOT NULL
);

CREATE TABLE products_orders (
    product_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

INSERT INTO categories (name, parent_category_id) VALUES ('Electronics', NULL);
INSERT INTO categories (name, parent_category_id) VALUES ('Computers', 1);
INSERT INTO categories (name, parent_category_id) VALUES ('Laptops', 1);
INSERT INTO categories (name, parent_category_id) VALUES ('Smartphones', 1);

INSERT INTO products (name, price, stock, category_id, image_url) VALUES ('MacBook Pro', 2500.00, 10, 3, 'https://example.com/macbook.jpg');
INSERT INTO products (name, price, stock, category_id, image_url) VALUES ('iPhone 14', 1200.00, 15, 4, 'https://example.com/iphone14.jpg');
INSERT INTO products (name, price, stock, category_id, image_url) VALUES ('Dell XPS 13', 1800.00, 8, 3, 'https://example.com/dellxps.jpg');

INSERT INTO orders (total_price, card_token, payment_status, payment_date, payment_gateway, status, buyer_email, seat_letter, seat_number)
VALUES (3700.00, 'tok_visa_12345', 'PAID', NOW(), 'STRIPE', 'FINISHED', 'buyer1@example.com', 'A', 12);

INSERT INTO orders (total_price, card_token, payment_status, payment_date, payment_gateway, status, buyer_email, seat_letter, seat_number)
VALUES (1200.00, 'tok_visa_67890', 'PENDING', NOW(), 'PAYPAL', 'OPEN', 'buyer2@example.com', 'B', 5);

INSERT INTO products_orders (product_id, order_id) VALUES (1, 1); -- MacBook Pro en primer pedido
INSERT INTO products_orders (product_id, order_id) VALUES (3, 1); -- Dell XPS en primer pedido
INSERT INTO products_orders (product_id, order_id) VALUES (2, 2); -- iPhone 14 en segundo pedido

