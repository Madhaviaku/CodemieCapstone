package com.library.catalog.service;

import com.library.catalog.dto.BookCreateRequest;
import com.library.catalog.dto.BookUpdateRequest;
import com.library.catalog.entity.Book;
import com.library.catalog.exception.BookNotFoundException;
import com.library.catalog.exception.DuplicateIsbnException;
import com.library.catalog.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void createBookPersistsNormalizedIsbn() {
        BookCreateRequest request = new BookCreateRequest(
                "978-0-13-468599-1",
                "Effective Java",
                "Joshua Bloch",
                "Addison-Wesley",
                LocalDate.of(2018, 1, 6),
                "Programming",
                5
        );
        when(bookRepository.existsByIsbn("9780134685991")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        var created = bookService.createBook(request);

        assertEquals("9780134685991", created.isbn());
        assertEquals("Effective Java", created.title());
        assertEquals(5, created.quantityAvailable());
    }

    @Test
    void createBookRejectsDuplicateIsbn() {
        when(bookRepository.existsByIsbn("9780134685991")).thenReturn(true);

        BookCreateRequest request = new BookCreateRequest(
                "9780134685991",
                "Effective Java",
                "Joshua Bloch",
                "Addison-Wesley",
                LocalDate.of(2018, 1, 6),
                "Programming",
                5
        );

        assertThrows(DuplicateIsbnException.class, () -> bookService.createBook(request));
    }

    @Test
    void createBookRejectsFuturePublicationDate() {
        BookCreateRequest request = new BookCreateRequest(
                "9780134685991",
                "Effective Java",
                "Joshua Bloch",
                null,
                LocalDate.now().plusDays(1),
                "Programming",
                1
        );

        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(request));
    }

    @Test
    void getBookFailsWhenMissing() {
        when(bookRepository.findByIsbn("9780134685991")).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookByIsbn("978-0-13-468599-1"));
    }

    @Test
    void updateBookRejectsNegativeQuantity() {
        Book existing = new Book("9780134685991", "Effective Java", "Joshua Bloch", "Addison-Wesley",
                LocalDate.of(2018, 1, 6), "Programming", 5);
        when(bookRepository.findByIsbn("9780134685991")).thenReturn(Optional.of(existing));

        BookUpdateRequest request = new BookUpdateRequest(null, null, null, null, null, -1);

        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook("9780134685991", request));
    }

    @Test
    void deleteBookFailsWhenMissing() {
        when(bookRepository.findByIsbn("0000000000")).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook("0000000000"));
    }
}
