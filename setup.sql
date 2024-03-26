DROP DATABASE IF EXISTS payment_gateway;

CREATE DATABASE payment_gateway;

USE payment_gateway;

CREATE TABLE wallet
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(256) UNIQUE,
    credential VARCHAR(256) NOT NULL,
    valid      BOOLEAN   DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE card
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id    BIGINT       NOT NULL,
    number       VARCHAR(256) NOT NULL,
    expiry_month VARCHAR(256) NOT NULL,
    expiry_year  VARCHAR(256) NOT NULL,
    holder_name  VARCHAR(256) NOT NULL,
    cvc          CHAR(3)      NOT NULL,
    valid        BOOLEAN   DEFAULT true,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_wallet_id_number_valid (wallet_id, number, valid)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

CREATE TABLE transaction
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id     BIGINT       NOT NULL,
    card_id       BIGINT       NOT NULL,
    platform      VARCHAR(256) NOT NULL COMMENT 'platform to finalise the transaction',
    type          VARCHAR(256) NOT NULL COMMENT 'type specified in platforms like Adyen',
    currency      VARCHAR(256) NOT NULL,
    value         BIGINT       NOT NULL COMMENT 'real value * 100',
    reference     VARCHAR(256) NOT NULL COMMENT 'refer to the transaction in platform',
    psp_reference VARCHAR(256),
    payload       VARCHAR(4096) COMMENT 'payload of transaction finalised or updated',
    status        VARCHAR(64)  NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_wallet_id_status (wallet_id, status),
    INDEX idx_card_id_status (card_id, status),
    INDEX idx_reference_status (reference, status)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


# seed data for testing

INSERT INTO wallet (email, credential)
values ('admin@example.com', '$2a$12$.Ig8F6C43.325s5dL/YlGe.BLEREfAvmaOr1B9scNDU6GPjT76k2u'),
       ('user1@example.com', '$2a$12$ysbFeFH4kfLGfrm2PPWHhuBHZHbS9Rjw6oxMZosnyeoezL2pR94vG');

INSERT INTO card (wallet_id, number, expiry_month, expiry_year, holder_name, cvc, valid)
VALUES ((SELECT id FROM wallet WHERE email = 'admin@example.com'),
        '4111111111111111', '03', '2020', 'John Smith', '737', false),
       ((SELECT id FROM wallet WHERE email = 'admin@example.com'),
        '4111111111111111', '03', '2030', 'John Smith', '737', true);

