INSERT INTO CATEGORY (id, name) VALUES
                                    (1, 'Smartphones'),
                                    (2, 'Laptops'),
                                    (3, 'Accessories');

INSERT INTO PRODUCT (id, name, description, price, stock, category_id) VALUES
                                                                           (1, 'iPhone 15', 'Apple smartphone', 999.99, 10, 1),
                                                                           (2, 'Samsung Galaxy S23', 'Samsung flagship', 899.99, 15, 1),
                                                                           (3, 'MacBook Air M3', 'Apple laptop', 1299.99, 5, 2),
                                                                           (4, 'Logitech MX Master 3', 'Ergonomic mouse', 119.99, 20, 3);
