CREATE TABLE product_images (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,

                                product_id BIGINT NOT NULL,

                                image_url VARCHAR(500) NOT NULL,

                                is_primary BOOLEAN DEFAULT FALSE,

                                display_order INT DEFAULT 0,

                                CONSTRAINT fk_product_image_product
                                    FOREIGN KEY (product_id)
                                        REFERENCES products(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_product_images_product
    ON product_images(product_id);

CREATE UNIQUE INDEX ux_product_primary_image
    ON product_images(product_id, is_primary);