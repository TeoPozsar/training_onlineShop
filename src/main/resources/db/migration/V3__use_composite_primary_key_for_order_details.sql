ALTER TABLE order_details
    DROP CONSTRAINT order_details_pkey,
    DROP COLUMN id;

ALTER TABLE order_details
    ADD CONSTRAINT pk_order_details PRIMARY KEY (order_id, product_id);
