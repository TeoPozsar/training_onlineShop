CREATE TABLE IF NOT EXISTS product_category (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS product (
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

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE  address (
        id UUID PRIMARY KEY,
        country VARCHAR(255) NOT NULL,
        city VARCHAR(255) NOT NULL,
        county VARCHAR(255) NOT NULL,
        street VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    address_id UUID NOT NULL,

    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id)
        REFERENCES users(id),
    CONSTRAINT fk_orders_address
        FOREIGN KEY (address_id)
        REFERENCES address(id)
);

CREATE TABLE IF NOT EXISTS location (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address_id UUID NOT NULL,

    CONSTRAINT fk_location_address
        FOREIGN KEY (address_id)
        REFERENCES address(id)
);


CREATE TABLE IF NOT EXISTS order_details (
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    shipped_from_location_id UUID NOT NULL,
    quantity INTEGER NOT NULL,

    CONSTRAINT pk_order_details
        PRIMARY KEY (order_id, product_id),
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

CREATE TABLE IF NOT EXISTS stock (
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