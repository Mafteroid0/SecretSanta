CREATE TABLE users(
    id  UUID    PRIMARY KEY,
    username    VARCHAR(50)    NOT NULL UNIQUE,
    display_name    VARCHAR(100)    NOT NULL,
    password_hash   VARCHAR(255)    NOT NULL
)

