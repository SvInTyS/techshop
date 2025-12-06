-- Очищаем таблицы (на всякий случай, чтобы не было дублей)
DELETE FROM order_item;
DELETE FROM orders;
DELETE FROM product;
DELETE FROM category;

-- Категории
INSERT INTO category (id, name) VALUES (1, 'Smartphones');
INSERT INTO category (id, name) VALUES (2, 'Laptops');
INSERT INTO category (id, name) VALUES (3, 'Accessories');

-- Товары с картинками
INSERT INTO product (id, name, description, price, stock, image_url, category_id)
VALUES
    (1,
     'iPhone 15',
     'Флагманский смартфон Apple с дисплеем OLED и отличной камерой.',
     999.99,
     10,
     'https://www.tptoner.at/userdata/products/1473/ah1331041-mtp03sk-6538d6041da5d.jpg?auto=compress&cs=tinysrgb&w=800',
     1
    );

INSERT INTO product (id, name, description, price, stock, image_url, category_id)
VALUES
    (2,
     'MacBook Air M3',
     'Лёгкий и тихий ноутбук для работы и учёбы.',
     1299.99,
     5,
     'https://avatars.mds.yandex.net/get-mpic/5332113/2a0000018eaeba54552860ab7b9b20cca5fd/orig?auto=compress&cs=tinysrgb&w=800',
     2
    );

INSERT INTO product (id, name, description, price, stock, image_url, category_id)
VALUES
    (3,
     'Logitech MX Master 3',
     'Эргономичная мышь для разработчиков и продвинутых пользователей.',
     119.99,
     20,
     'https://static.onlinetrade.ru/img/items/m/mysh_besprovodnaya_logitech_mx_master_3s_mid_grey_910_006560__2463090_3.jpg?auto=compress&cs=tinysrgb&w=800',
     3
    );

INSERT INTO product (id, name, description, price, stock, image_url, category_id)
VALUES
    (4,
     'Dell XPS 13',
     'Компактный ультрабук с тонкими рамками и хорошей автономностью.',
     1399.99,
     7,
     'https://micro-line.ru/images/thumbnails/1200/1200/detailed/6530/3470013.jpg?auto=compress&cs=tinysrgb&w=800',
     2
    );