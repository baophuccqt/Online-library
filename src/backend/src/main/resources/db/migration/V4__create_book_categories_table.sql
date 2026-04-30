-- V4: Create book_categories join table (many-to-many)
CREATE TABLE book_categories (
                                 book_id         BIGINT      NOT NULL REFERENCES books(id) ON DELETE CASCADE,
                                 category_id     BIGINT      NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
                                 PRIMARY KEY (book_id, category_id)
);

CREATE INDEX idx_book_categories_category ON book_categories(category_id);