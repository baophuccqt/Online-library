-- V3: Create books table
CREATE TABLE books (
                       id                  BIGSERIAL       PRIMARY KEY,
                       isbn                VARCHAR(20)     UNIQUE,
                       title               VARCHAR(500)    NOT NULL,
                       author              VARCHAR(255)    NOT NULL,
                       description         TEXT,
                       cover_url           VARCHAR(1000),
                       publisher           VARCHAR(255),
                       publish_year        INTEGER,
                       language            VARCHAR(50)     DEFAULT 'Vietnamese',
                       total_copies        INTEGER         NOT NULL DEFAULT 1,
                       available_copies    INTEGER         NOT NULL DEFAULT 1,
                       created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
                       updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),

                       CONSTRAINT chk_copies_positive CHECK (total_copies >= 0),
                       CONSTRAINT chk_available_copies CHECK (available_copies >= 0),
                       CONSTRAINT chk_available_le_total CHECK (available_copies <= total_copies)
);

CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_isbn ON books(isbn);