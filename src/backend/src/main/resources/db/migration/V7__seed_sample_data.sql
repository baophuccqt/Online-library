-- V7: Seed sample data for development

-- Admin account (password: admin123 - BCrypt hash)
INSERT INTO users (email, password_hash, full_name, role) VALUES
    ('admin@library.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Admin', 'ADMIN');

-- Sample categories
INSERT INTO categories (name, description) VALUES
                                               ('Fiction', 'Novels, short stories, and literary works'),
                                               ('Science', 'Natural sciences, physics, chemistry, biology'),
                                               ('Technology', 'Computer science, engineering, IT'),
                                               ('History', 'Historical events, biographies, civilizations'),
                                               ('Philosophy', 'Philosophy, ethics, critical thinking'),
                                               ('Self-help', 'Personal development, productivity, motivation');

-- Sample books (data from Open Library API)
INSERT INTO books (isbn, title, author, description, publisher, publish_year, language, total_copies, available_copies) VALUES
                                                                                                                            ('9780061120084', 'To Kill a Mockingbird', 'Harper Lee', 'A classic of modern American literature about racial injustice in the Deep South.', 'Harper Perennial', 1960, 'English', 5, 5),
                                                                                                                            ('9780451524935', '1984', 'George Orwell', 'A dystopian novel set in a totalitarian society ruled by Big Brother.', 'Signet Classic', 1949, 'English', 3, 3),
                                                                                                                            ('9780140283334', 'Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 'A narrative history of humankind from the Stone Age to the present.', 'Vintage', 2014, 'English', 4, 4),
                                                                                                                            ('9780132350884', 'Clean Code', 'Robert C. Martin', 'A handbook of agile software craftsmanship.', 'Prentice Hall', 2008, 'English', 3, 3),
                                                                                                                            ('9780596007126', 'Head First Design Patterns', 'Eric Freeman', 'A brain-friendly guide to design patterns.', 'O''Reilly Media', 2004, 'English', 2, 2),
                                                                                                                            ('9780385533225', 'The Road', 'Cormac McCarthy', 'A post-apocalyptic tale of a father and son journey.', 'Vintage', 2006, 'English', 2, 2);

-- Link books to categories
INSERT INTO book_categories (book_id, category_id) VALUES
                                                       (1, 1), -- To Kill a Mockingbird -> Fiction
                                                       (2, 1), -- 1984 -> Fiction
                                                       (3, 4), -- Sapiens -> History
                                                       (3, 5), -- Sapiens -> Philosophy
                                                       (4, 3), -- Clean Code -> Technology
                                                       (5, 3), -- Head First Design Patterns -> Technology
                                                       (6, 1); -- The Road -> Fiction