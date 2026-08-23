CREATE TABLE wishlist_items (
        id UUID PRIMARY KEY,
        user_id UUID NOT NULL,
        name VARCHAR(200) NOT NULL,
        description TEXT,

        CONSTRAINT fk_wishlist_item_user
            FOREIGN KEY (user_id)
                REFERENCES users(id)
                ON DELETE CASCADE
);