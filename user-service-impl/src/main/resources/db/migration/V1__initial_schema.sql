-- =========================
-- V1__init_schema.sql
-- Flyway baseline schema
-- Compatible with Hibernate validate + auditing
-- =========================


-- USERS (extends BaseEntity → includes auditing fields)
CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       username VARCHAR(255) NOT NULL,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       is_active TINYINT(1),

                       created_at DATETIME(6) NOT NULL,
                       updated_at DATETIME(6),
                       created_by VARCHAR(255),
                       updated_by VARCHAR(255),

                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_email (email),
                       UNIQUE KEY uk_users_username (username)
);



-- ROLES
CREATE TABLE roles (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(255) NOT NULL,
                       description VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



-- USER_ROLES (many-to-many)
CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),

                            CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
                            CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id)
);



-- ADDRESSES
CREATE TABLE addresses (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           user_id BIGINT NOT NULL,

                           address_line1 VARCHAR(255) NOT NULL,
                           address_line2 VARCHAR(255),
                           city VARCHAR(100) NOT NULL,
                           state VARCHAR(100) NOT NULL,
                           country VARCHAR(100) NOT NULL,
                           pincode VARCHAR(10) NOT NULL,

                           is_default BOOLEAN NOT NULL,
                           is_deleted BOOLEAN NOT NULL,
                           deleted_at TIMESTAMP,

                           CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id)
);



-- PASSWORD_RESET_TOKENS
CREATE TABLE password_reset_tokens (
                                       id BIGINT PRIMARY KEY AUTO_INCREMENT,

                                       token VARCHAR(255) NOT NULL UNIQUE,
                                       user_id BIGINT NOT NULL,
                                       expiry_date TIMESTAMP NOT NULL,
                                       used BOOLEAN NOT NULL,
                                       created_at TIMESTAMP NOT NULL,

                                       CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users(id)
);



-- REFRESH_TOKEN
CREATE TABLE refresh_token (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,

                               token VARCHAR(255) NOT NULL UNIQUE,
                               user_id BIGINT NOT NULL,
                               expiry_date TIMESTAMP,
                               created_at TIMESTAMP NOT NULL,

                               CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id)
);