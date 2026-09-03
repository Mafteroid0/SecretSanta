ALTER TABLE wishlist_items
    ADD COLUMN source_type VARCHAR(30) NOT NULL DEFAULT 'MANUAL';

ALTER TABLE wishlist_items
    ADD COLUMN source_url TEXT;

ALTER TABLE wishlist_items
    ADD COLUMN external_id VARCHAR(255);