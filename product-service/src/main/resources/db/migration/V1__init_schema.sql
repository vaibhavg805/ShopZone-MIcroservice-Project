CREATE TABLE categories(
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL UNIQUE,
                           description TEXT,
                           image_url VARCHAR(500),
                           is_active BOOLEAN DEFAULT TRUE,
                           parent_id BIGINT,
                           created_at TIMESTAMP NOT NULL ,
                           updated_at TIMESTAMP NOT NULL ,
                           CONSTRAINT fk_category_parent
                               FOREIGN KEY (parent_id)
                                   REFERENCES categories(id)
);