package com.library.catalog.controller;

import com.library.catalog.dto.ApiResponse;
import com.library.catalog.dto.BookCreateRequest;
import com.library.catalog.dto.BookPageResponse;
import com.library.catalog.dto.BookResponse;
import com.library.catalog.dto.BookUpdateRequest;
import com.library.catalog.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<BookPageResponse>> getBooks(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (q == null && genre == null && yearFrom == null && yearTo == null) {
            return ResponseEntity.ok(ApiResponse.success(200, "Books retrieved successfully", bookService.getBooks(page, size)));
        }

        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Search completed successfully",
                bookService.searchCatalog(q, genre, yearFrom, yearTo, page, size)));
    }

    @GetMapping("/books/search")
    public ResponseEntity<ApiResponse<BookPageResponse>> searchBooks(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Search completed successfully",
                bookService.searchBooks(title, page, size)));
    }

    @GetMapping("/books/{isbn}")
    public ResponseEntity<ApiResponse<BookResponse>> getBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(ApiResponse.success(200, "Book retrieved successfully", bookService.getBookByIsbn(isbn)));
    }

    @PostMapping("/books")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody BookCreateRequest request) {
        BookResponse created = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Book created successfully", created));
    }

    @PutMapping("/books/{isbn}")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable String isbn,
            @Valid @RequestBody BookUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(200, "Book updated successfully", bookService.updateBook(isbn, request)));
    }

    @DeleteMapping("/books/{isbn}")
    public ResponseEntity<Void> deleteBook(@PathVariable String isbn) {
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }
}
