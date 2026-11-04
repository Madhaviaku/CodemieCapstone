-- Sample books loaded by the dev profile. ISBN values are stored as digits only.
INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780134685991', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', DATE '2018-01-06', 'Programming', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780134685991');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780596009205', 'Head First Java', 'Kathy Sierra', 'O''Reilly', DATE '2005-02-09', 'Programming', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780596009205');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780132350884', 'Clean Code', 'Robert C. Martin', 'Prentice Hall', DATE '2008-08-01', 'Programming', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780132350884');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780201633610', 'Design Patterns', 'Erich Gamma', 'Addison-Wesley', DATE '1994-10-31', 'Software Engineering', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780201633610');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780134494166', 'Clean Architecture', 'Robert C. Martin', 'Prentice Hall', DATE '2017-09-20', 'Software Engineering', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780134494166');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780134757599', 'Refactoring', 'Martin Fowler', 'Addison-Wesley', DATE '2018-11-30', 'Programming', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780134757599');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9781617294945', 'Spring in Action', 'Craig Walls', 'Manning', DATE '2018-10-01', 'Programming', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9781617294945');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9781492056270', 'Learning React', 'Alex Banks', 'O''Reilly', DATE '2020-06-12', 'Programming', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9781492056270');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780596517748', 'JavaScript: The Good Parts', 'Douglas Crockford', 'O''Reilly', DATE '2008-05-01', 'Programming', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780596517748');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780135957059', 'The Pragmatic Programmer', 'David Thomas', 'Addison-Wesley', DATE '2019-09-13', 'Software Engineering', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780135957059');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780262033848', 'Introduction to Algorithms', 'Thomas H. Cormen', 'MIT Press', DATE '2009-07-31', 'Computer Science', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780262033848');

INSERT INTO books (isbn, title, author, publisher, publication_date, genre, quantity_available, created_at, updated_at)
SELECT '9780743273565', 'The Great Gatsby', 'F. Scott Fitzgerald', 'Scribner', DATE '1925-04-10', 'Fiction', 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = '9780743273565');
