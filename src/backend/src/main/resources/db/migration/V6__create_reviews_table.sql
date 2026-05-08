-- V6: Create reviews table
CREATE TABLE reviews (
                         id              BIGSERIAL       PRIMARY KEY,
                         user_id         BIGINT          NOT NULL REFERENCES users(id),
                         book_id         BIGINT          NOT NULL REFERENCES books(id),
                         rating          INTEGER         NOT NULL,
                         comment         TEXT,
                         created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),

                         CONSTRAINT chk_rating_range CHECK (rating >= 1 AND rating <= 5),
                         CONSTRAINT uq_user_book_review UNIQUE (user_id, book_id)  -- 1 review per user per book
);

CREATE INDEX idx_reviews_book ON reviews(book_id);
CREATE INDEX idx_reviews_user ON reviews(user_id);