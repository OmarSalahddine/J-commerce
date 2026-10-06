CREATE TABLE orders (
    id             BIGSERIAL      PRIMARY KEY,
    customer_email VARCHAR(255)   NOT NULL,
    status         VARCHAR(20)    NOT NULL CHECK (status IN ('PENDING', 'PAID', 'CANCELLED')),
    total_amount   NUMERIC(12, 2) NOT NULL CHECK (total_amount >= 0),
    created_at     TIMESTAMPTZ    NOT NULL,
    updated_at     TIMESTAMPTZ    NOT NULL,
    version        INTEGER        NOT NULL DEFAULT 0
);

CREATE TABLE order_lines (
    id           BIGSERIAL      PRIMARY KEY,
    order_id     BIGINT         NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    product_id   BIGINT         NOT NULL REFERENCES products (id) ON DELETE RESTRICT,
    product_sku  VARCHAR(64)    NOT NULL,
    product_name VARCHAR(255)   NOT NULL,
    unit_price   NUMERIC(12, 2) NOT NULL CHECK (unit_price >= 0),
    quantity     INTEGER        NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_orders_customer_email ON orders (customer_email);
CREATE INDEX idx_order_lines_order_id  ON order_lines (order_id);