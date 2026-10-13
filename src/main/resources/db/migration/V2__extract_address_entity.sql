CREATE TABLE address (
    id UUID PRIMARY KEY,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    county VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL
);

ALTER TABLE location
    ADD COLUMN address_id UUID NOT NULL;

ALTER TABLE orders
    ADD COLUMN address_id UUID NOT NULL;

ALTER TABLE location
    ADD CONSTRAINT fk_location_address
        FOREIGN KEY (address_id)
        REFERENCES address(id);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_address
        FOREIGN KEY (address_id)
        REFERENCES address(id);

ALTER TABLE location
    DROP COLUMN address_country,
    DROP COLUMN address_city,
    DROP COLUMN address_county,
    DROP COLUMN address_street;

ALTER TABLE orders
    DROP COLUMN address_country,
    DROP COLUMN address_city,
    DROP COLUMN address_county,
    DROP COLUMN address_street;