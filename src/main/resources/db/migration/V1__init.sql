CREATE TABLE product_metadata (
    product_id VARCHAR(50) PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    brand VARCHAR(100) NOT NULL
);

-- Supports optional category and brand filtering.
CREATE INDEX idx_category_brand ON product_metadata(category, brand);


CREATE TABLE shopper_shelf (
    shopper_id VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    relevancy_score DOUBLE PRECISION NOT NULL,
    PRIMARY KEY (shopper_id, product_id),
    CONSTRAINT fk_shelf_product
        FOREIGN KEY (product_id)
        REFERENCES product_metadata(product_id)
);

-- Supports the main read path: find products for one shopper ordered by relevancy score.
CREATE INDEX idx_shopper_relevancy ON shopper_shelf(shopper_id, relevancy_score DESC);