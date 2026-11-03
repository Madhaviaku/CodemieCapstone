package com.library.catalog.config;

import com.library.catalog.dto.BookCreateRequest;
import com.library.catalog.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("dev")
public class DevDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataLoader.class);

    private final BookService bookService;

    public DevDataLoader(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public void run(String... args) {
        if (bookService.getBooks(0, 1).totalElements() > 0) {
            return;
        }
        List<BookCreateRequest> books = sampleBooks();
        books.forEach(bookService::createBook);
        log.info("Seeded {} sample books", books.size());
    }

    private List<BookCreateRequest> sampleBooks() {
        return List.of(
                book("9780134685991", "Effective Java", "Joshua Bloch", "Addison-Wesley", "2018-01-06", "Programming", 5),
                book("9780596009205", "Head First Java", "Kathy Sierra", "O'Reilly", "2005-02-09", "Programming", 3),
                book("9780132350884", "Clean Code", "Robert C. Martin", "Prentice Hall", "2008-08-01", "Programming", 8),
                book("9780201633610", "Design Patterns", "Erich Gamma", "Addison-Wesley", "1994-10-31", "Software Engineering", 4),
                book("9780134494166", "Clean Architecture", "Robert C. Martin", "Prentice Hall", "2017-09-20", "Software Engineering", 6),
                book("9780134757599", "Refactoring", "Martin Fowler", "Addison-Wesley", "2018-11-30", "Programming", 2),
                book("9781617294945", "Spring in Action", "Craig Walls", "Manning", "2018-10-01", "Programming", 7),
                book("9781492056270", "Learning React", "Alex Banks", "O'Reilly", "2020-06-12", "Programming", 4),
                book("9780596517748", "JavaScript: The Good Parts", "Douglas Crockford", "O'Reilly", "2008-05-01", "Programming", 1),
                book("9780135957059", "The Pragmatic Programmer", "David Thomas", "Addison-Wesley", "2019-09-13", "Software Engineering", 9),
                book("9780262033848", "Introduction to Algorithms", "Thomas H. Cormen", "MIT Press", "2009-07-31", "Computer Science", 3),
                book("9780743273565", "The Great Gatsby", "F. Scott Fitzgerald", "Scribner", "1925-04-10", "Fiction", 12)
        );
    }

    private BookCreateRequest book(String isbn, String title, String author, String publisher, String date,
                                   String genre, int quantity) {
        return new BookCreateRequest(isbn, title, author, publisher, LocalDate.parse(date), genre, quantity);
    }
}
