CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(50) UNIQUE,
                       email VARCHAR(100) UNIQUE,
                       password VARCHAR(255) NOT NULL
);
