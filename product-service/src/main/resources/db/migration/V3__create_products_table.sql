CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          category_id BIGINT NOT NULL,
                          brand_id BIGINT,
                          base_price DECIMAL(10,2) NOT NULL,
                          discount_percentage DECIMAL(5,2) DEFAULT 0,
                          sku VARCHAR(100) NOT NULL UNIQUE,
                          is_active BOOLEAN DEFAULT TRUE,
                          specifications TEXT,
                          average_rating DECIMAL(3,2) DEFAULT 0,
                          review_count INT DEFAULT 0,
                          created_at TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP NOT NULL,

                          CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
                          CONSTRAINT fk_product_brand FOREIGN KEY (brand_id) REFERENCES brands(id)
);