package com.library.catalog.service;

import com.library.catalog.dto.BookCreateRequest;
import com.library.catalog.dto.BookPageResponse;
import com.library.catalog.dto.BookResponse;
import com.library.catalog.dto.BookUpdateRequest;
import com.library.catalog.entity.Book;
import com.library.catalog.exception.BookNotFoundException;
import com.library.catalog.exception.DuplicateIsbnException;
import com.library.catalog.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public BookPageResponse getBooks(int page, int size) {
        Page<BookResponse> books = repository.findAll(pageable(page, size)).map(this::toResponse);
        log.info("Listed books page={} size={} total={}", page, size, books.getTotalElements());
        return BookPageResponse.from(books);
    }

    public BookResponse getBookByIsbn(String isbn) {
        String normalizedIsbn = normalizeIsbn(isbn);
        Book book = repository.findByIsbn(normalizedIsbn)
                .orElseThrow(() -> new BookNotFoundException(normalizedIsbn));
        return toResponse(book);
    }

    @Transactional
    public BookResponse createBook(BookCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        String isbn = normalizeIsbn(request.isbn());
        String title = requireText(request.title(), "Title", 200);
        String author = requireText(request.author(), "Author", 100);
        String genre = requireText(request.genre(), "Genre", 50);
        String publisher = optionalText(request.publisher(), "Publisher", 100);
        LocalDate publicationDate = requirePastOrPresent(request.publicationDate());
        int quantity = requireQuantity(request.quantityAvailable());

        if (repository.existsByIsbn(isbn)) {
            throw new DuplicateIsbnException(isbn);
        }

        Book book = new Book(isbn, title, author, publisher, publicationDate, genre, quantity);
        Book saved = repository.save(book);
        log.info("Created book isbn={}", isbn);
        return toResponse(saved);
    }

    @Transactional
    public BookResponse updateBook(String isbn, BookUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        String normalizedIsbn = normalizeIsbn(isbn);
        Book existing = repository.findByIsbn(normalizedIsbn)
                .orElseThrow(() -> new BookNotFoundException(normalizedIsbn));

        if (request.title() != null) {
            existing.setTitle(requireText(request.title(), "Title", 200));
        }
        if (request.author() != null) {
            existing.setAuthor(requireText(request.author(), "Author", 100));
        }
        if (request.publisher() != null) {
            existing.setPublisher(optionalText(request.publisher(), "Publisher", 100));
        }
        if (request.publicationDate() != null) {
            existing.setPublicationDate(requirePastOrPresent(request.publicationDate()));
        }
        if (request.genre() != null) {
            existing.setGenre(requireText(request.genre(), "Genre", 50));
        }
        if (request.quantityAvailable() != null) {
            existing.setQuantityAvailable(requireQuantity(request.quantityAvailable()));
        }
        existing.setUpdatedAt(LocalDateTime.now());

        Book saved = repository.saveAndFlush(existing);
        log.info("Updated book isbn={}", normalizedIsbn);
        return toResponse(saved);
    }

    @Transactional
    public void deleteBook(String isbn) {
        String normalizedIsbn = normalizeIsbn(isbn);
        Book existing = repository.findByIsbn(normalizedIsbn)
                .orElseThrow(() -> new BookNotFoundException(normalizedIsbn));
        repository.delete(existing);
        log.info("Deleted book isbn={}", normalizedIsbn);
    }

    public BookPageResponse searchBooks(String title, int page, int size) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Search title is required");
        }
        String term = title.trim();
        Page<BookResponse> books = repository
                .findByTitleContainingIgnoreCase(term, pageable(page, size))
                .map(this::toResponse);
        log.info("Searched books title='{}' page={} size={} total={}", term, page, size, books.getTotalElements());
        return BookPageResponse.from(books);
    }

    public BookPageResponse searchBooks(String q, String genre, Integer yearMin, Integer yearMax, int page, int size) {
        if (yearMin != null && yearMax != null && yearMin > yearMax) {
            throw new IllegalArgumentException("Min year cannot be greater than max year");
        }
        String keyword = blankToNull(q);
        String isbn = keyword == null ? null : keyword.replace("-", "").replace(" ", "");
        String genreFilter = blankToNull(genre);
        Page<BookResponse> books = repository
                .search(keyword, isbn, genreFilter, yearMin, yearMax, pageable(page, size))
                .map(this::toResponse);
        log.info(
                "Filtered catalog q='{}' genre='{}' yearMin={} yearMax={} page={} size={} total={}",
                keyword, genreFilter, yearMin, yearMax, page, size, books.getTotalElements());
        return BookPageResponse.from(books);
    }

    private String blankToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public String normalizeIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        String normalized = isbn.replace("-", "").trim();
        if (!normalized.matches("\\d{10}|\\d{13}")) {
            throw new IllegalArgumentException("Invalid ISBN format");
        }
        return normalized;
    }

    private Pageable pageable(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be zero or greater");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        Sort sort = Sort.by(Sort.Order.asc("title").ignoreCase(), Sort.Order.asc("isbn"));
        return PageRequest.of(page, size, sort);
    }

    private String requireText(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(field + " length must not exceed " + maxLength + " characters");
        }
        return trimmed;
    }

    private String optionalText(String value, String field, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(field + " length must not exceed " + maxLength + " characters");
        }
        return trimmed;
    }

    private LocalDate requirePastOrPresent(LocalDate publicationDate) {
        if (publicationDate != null && publicationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Publication date cannot be in the future");
        }
        return publicationDate;
    }

    private int requireQuantity(Integer quantityAvailable) {
        int quantity = quantityAvailable == null ? 0 : quantityAvailable;
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        return quantity;
    }

    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublicationDate(),
                book.getGenre(),
                book.getQuantityAvailable(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }
}
