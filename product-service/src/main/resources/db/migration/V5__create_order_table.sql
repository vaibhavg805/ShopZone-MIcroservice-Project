CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,

                        user_id BIGINT NOT NULL,
                        product_id BIGINT NOT NULL,
                        quantity INT NOT NULL,
                        total_price DOUBLE,

                        status VARCHAR(50),
                        address VARCHAR(255),

                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL
);