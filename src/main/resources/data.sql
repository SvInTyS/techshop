-- =========================
-- Категории
-- =========================
INSERT INTO category (name)
SELECT 'Smartphones'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Smartphones');

INSERT INTO category (name)
SELECT 'Laptops'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Laptops');

INSERT INTO category (name)
SELECT 'Accessories'
    WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = 'Accessories');

-- =========================
-- Товары
-- =========================
INSERT INTO product (name, description, price, stock, category_id)
SELECT 'iPhone 15', 'Apple smartphone', 999.99, 10,
       (SELECT id FROM category WHERE name = 'Smartphones')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'iPhone 15');

INSERT INTO product (name, description, price, stock, category_id)
SELECT 'Samsung Galaxy S24', 'Android flagship', 899.99, 15,
       (SELECT id FROM category WHERE name = 'Smartphones')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Samsung Galaxy S24');

INSERT INTO product (name, description, price, stock, category_id)
SELECT 'MacBook Air M3', 'Apple laptop', 1299.99, 5,
       (SELECT id FROM category WHERE name = 'Laptops')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'MacBook Air M3');

INSERT INTO product (name, description, price, stock, category_id)
SELECT 'Dell XPS 13', 'Ultrabook', 1199.99, 7,
       (SELECT id FROM category WHERE name = 'Laptops')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Dell XPS 13');

INSERT INTO product (name, description, price, stock, category_id)
SELECT 'Logitech MX Master 3', 'Ergonomic mouse', 119.99, 20,
       (SELECT id FROM category WHERE name = 'Accessories')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Logitech MX Master 3');

INSERT INTO product (name, description, price, stock, category_id)
SELECT 'Apple AirPods Pro 2', 'Wireless earbuds', 249.99, 30,
       (SELECT id FROM category WHERE name = 'Accessories')
    WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = 'Apple AirPods Pro 2');
