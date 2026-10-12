CREATE TABLE product_category (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE product (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(19, 2) NOT NULL,
    weight DOUBLE PRECISION,
    category_id UUID NOT NULL ,
    image_url VARCHAR(2048),

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES product_category(id),
    CONSTRAINT  chk_product_price_positive
        CHECK (price >= 0),
    CONSTRAINT  chk_product_weight
        CHECK (weight >= 0)
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    address_country VARCHAR(255) NOT NULL,
    address_city VARCHAR(255) NOT NULL,
    address_county VARCHAR(255) NOT NULL,
    address_street VARCHAR(255) NOT NULL,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
);

CREATE TABLE location (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address_country VARCHAR(255) NOT NULL,
    address_city VARCHAR(255) NOT NULL,
    address_county VARCHAR(255) NOT NULL,
    address_street VARCHAR(255) NOT NULL
);


CREATE TABLE order_details (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    shipped_from_location_id UUID NOT NULL,
    quantity INTEGER NOT NULL,

    CONSTRAINT fk_order_details_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id),
    CONSTRAINT fk_order_details_product
        FOREIGN KEY (product_id)
        REFERENCES product(id),
    CONSTRAINT fk_order_details_location
        FOREIGN KEY (shipped_from_location_id)
        REFERENCES location(id),
    CONSTRAINT chk_order_details_quantity
        CHECK (quantity > 0)
);

CREATE TABLE stock (
    product_id UUID NOT NULL,
    location_id UUID NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (product_id, location_id),

    CONSTRAINT fk_stock_product
        FOREIGN KEY (product_id)
        REFERENCES product(id),
    CONSTRAINT fk_stock_location
        FOREIGN KEY (location_id)
        REFERENCES location(id),
    CONSTRAINT chk_stock_quantity
        CHECK (quantity >= 0)
);