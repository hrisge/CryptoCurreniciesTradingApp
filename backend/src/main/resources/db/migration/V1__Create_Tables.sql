CREATE SCHEMA IF NOT EXISTS cryptotrading;

CREATE TABLE IF NOT EXISTS cryptotrading.application_user (
    id SERIAL PRIMARY KEY,
    balance NUMERIC(19,2) NOT NULL DEFAULT 10000.00,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cryptotrading.holding (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(255) NOT NULL,
    quantity NUMERIC(19,6) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_holding_user FOREIGN KEY(user_id) REFERENCES cryptotrading.application_user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cryptotrading.transaction_history (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    ticker VARCHAR(255) NOT NULL,
    time TIMESTAMP NOT NULL,
    bought BOOLEAN NOT NULL,
    quantity NUMERIC(19,6) NOT NULL,
    price NUMERIC(19,6) NOT NULL,
    CONSTRAINT fk_transaction_user FOREIGN KEY(user_id) REFERENCES cryptotrading.application_user(id) ON DELETE CASCADE
);
