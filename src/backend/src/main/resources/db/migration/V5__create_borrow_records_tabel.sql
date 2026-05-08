-- V5: Create borrow_records table
CREATE TABLE borrow_records (
                                id              BIGSERIAL       PRIMARY KEY,
                                user_id         BIGINT          NOT NULL REFERENCES users(id),
                                book_id         BIGINT          NOT NULL REFERENCES books(id),
                                borrow_date     TIMESTAMP       NOT NULL DEFAULT NOW(),
                                due_date        TIMESTAMP       NOT NULL,
                                return_date     TIMESTAMP,
                                status          VARCHAR(20)     NOT NULL DEFAULT 'BORROWED',  -- BORROWED | RETURNED | OVERDUE

                                CONSTRAINT chk_due_after_borrow CHECK (due_date > borrow_date)
);

CREATE INDEX idx_borrow_user ON borrow_records(user_id);
CREATE INDEX idx_borrow_book ON borrow_records(book_id);
CREATE INDEX idx_borrow_status ON borrow_records(status);