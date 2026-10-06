CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    sku         VARCHAR(64)    NOT NULL UNIQUE,
    name        VARCHAR(255)   NOT NULL,
    description TEXT,
    price       NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    stock       INTEGER        NOT NULL DEFAULT 0 CHECK (stock >= 0),
    category    VARCHAR(100),
    created_at  TIMESTAMPTZ    NOT NULL,
    updated_at  TIMESTAMPTZ    NOT NULL,
    version     INTEGER        NOT NULL DEFAULT 0
);

-- CREATE TABLE orders();
-- CREATE TABLE order_items();
-- CREATE TABLE idempotency_keys();

CREATE INDEX idx_products_category ON products (category);

