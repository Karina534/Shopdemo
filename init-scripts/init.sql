-- Таблица publishers
CREATE TABLE publishers (
                            publisher_id SERIAL PRIMARY KEY,
                            publisher_name VARCHAR(100) NOT NULL
);

-- Таблица currencies
CREATE TABLE currencies (
                            currency_id SERIAL PRIMARY KEY,
                            currency_name CHAR(1) NOT NULL
);

-- Таблица books
CREATE TABLE books (
                       book_id serial primary key,
                       author varchar(100) NOT NULL,
                       title varchar(100) NOT NULL,
                       description TEXT,
                       price numeric(10, 2) NOT NULL,
                       currency_id int NOT NULL default 1,
                       remains int NOT NULL CHECK ( remains >= 0 ),
                       publisher_id int,

                       FOREIGN KEY (currency_id) REFERENCES currencies (currency_id) on delete set default,
                       FOREIGN KEY (publisher_id) REFERENCES publishers (publisher_id) on delete set null
);

-- Таблица books_images
CREATE TABLE books_images (
                              books_images_id SERIAL PRIMARY KEY,
                              book_id INTEGER REFERENCES books(book_id),
                              image_url TEXT
);

-- Таблица consumers
CREATE TABLE consumers (
                           consumer_id SERIAL PRIMARY KEY,
                           consumer_name VARCHAR(50) NOT NULL,
                           surname VARCHAR(100) NOT NULL,
                           email VARCHAR(200) UNIQUE NOT NULL,
                           password VARCHAR(200) NOT NULL,
                           telephone VARCHAR(12) UNIQUE NOT NULL,
                           role CHAR(1)
);

-- Таблица statuses
CREATE TABLE statuses (
                          status_id SERIAL PRIMARY KEY,
                          status_name VARCHAR(50) NOT NULL
);

-- Таблица orders
CREATE TABLE orders (
                        order_id SERIAL PRIMARY KEY,
                        total_price NUMERIC(10, 2) NOT NULL CHECK ( total_price >= 0 ),
                        creation_date DATE NOT NULL DEFAULT CURRENT_DATE,
                        end_date DATE NOT NULL DEFAULT CURRENT_DATE,
                        status_id INTEGER REFERENCES statuses(status_id),
                        consumer_id INTEGER REFERENCES consumers(consumer_id),
                        address VARCHAR(300)
);

-- Таблица ordersbooks
CREATE TABLE ordersbooks (
                             order_book_id SERIAL PRIMARY KEY,
                             order_id INTEGER REFERENCES orders(order_id),
                             book_id INTEGER REFERENCES books(book_id),
                             quantity INTEGER NOT NULL CHECK ( quantity > 0 )
);

-- Таблица baskets
CREATE TABLE baskets (
                         basket_id SERIAL PRIMARY KEY,
                         consumer_id INTEGER REFERENCES consumers(consumer_id)
);

-- Таблица basketbooks
CREATE TABLE basketbooks (
                             basketbooks_id SERIAL PRIMARY KEY,
                             basket_id INTEGER REFERENCES baskets(basket_id),
                             book_id INTEGER REFERENCES books(book_id),
                             quantity INTEGER NOT NULL CHECK ( quantity > 0 )
);

-- Таблица admins_ind_nums
CREATE TABLE admins_ind_nums (
                                 admins_ind_nums_id SERIAL PRIMARY KEY,
                                 ind_num INTEGER UNIQUE NOT NULL
);

-- Таблица admins
CREATE TABLE admins (
                        admin_id SERIAL PRIMARY KEY,
                        admin_name VARCHAR(50) NOT NULL,
                        surname VARCHAR(100) NOT NULL,
                        email VARCHAR(200) UNIQUE NOT NULL,
                        password VARCHAR(200) NOT NULL,
                        telephone VARCHAR(12) NOT NULL,
                        role CHAR(1),
                        individual_num_id INTEGER REFERENCES admins_ind_nums(admins_ind_nums_id) NOT NULL UNIQUE
);

INSERT INTO admins_ind_nums (ind_num)
VALUES (123);

INSERT INTO admins (admin_name, surname, email, password, telephone, role, individual_num_id)
VALUES ('TestAdmin', 'TestAdmin', 'testadmin@mail.ru', '10293847', '+79159159595', 'A', 1);
-- ('TestUser, email = 'testUser@mail.ru', password = '10293847')
INSERT INTO currencies
VALUES (1, 'P'),
       (2, '$');

INSERT INTO publishers (publisher_name)
VALUES ('Махаон'), ('Росмэн'), ('АСТ'), ('Азбука');

INSERT INTO books (author, title, description, price, currency_id, remains, publisher_id)
VALUES ('Джуанн Роулинг', 'Гарри Поттер и Философский камень', '
Первая часть из серии книг о Гарри Поттере. Одиннадцатилетний мальчик-сирота Гарри Поттер живет в семье своей тетки и даже не подозревает, что он - настоящий волшебник. Но однажды прилетает сова с письмом для него, и жизнь Гарри Поттера изменяется навсегда. Гарри узнает, что он волшебник, встречает близких друзей и немало врагов в Школе Чародейства и Волшебства «Хогвартс», а также с помощью своих друзей пресекает попытку возвращения злого волшебника Лорда Волан-де-Морта. Перевод от издательства РОСМЭН. Этот перевод использован для российского дубляжа всех частей экранизации. Переводчикам удалось точно передать смысл текста, не перегружая его при этом сложными конструкциями. Читать роман легко и приятно благодаря красивому литературному слогу и удачно переведенными именами и названиями. Больше книги в этом переводе не издаются, а перевод нынешнего издательства не нравится многим читателям. Книга в идеальном состоянии. Послужит отличным подарком на любой праздник!',
        1394, 1, 50, 2);