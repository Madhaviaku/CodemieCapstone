package com.library.catalog.service;

import com.library.catalog.dto.BookResponse;
import com.library.catalog.dto.CatalogPageResponse;
import com.library.catalog.entity.Book;
import com.library.catalog.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    private final BookRepository repository;

    public CatalogService(BookRepository repository) {
        this.repository = repository;
    }

    public CatalogPageResponse getCatalog(String q, String genre, int page, int pageSize) {
        String normalizedQuery = normalizeQuery(q);
        String normalizedGenre = normalizeGenre(genre);

        Page<Book> result = repository.searchCatalog(normalizedQuery, normalizedGenre, pageable(page, pageSize));

        return new CatalogPageResponse(
                result.map(this::toResponse).getContent(),
                result.getTotalElements(),
                result.getNumber() + 1,
                result.getSize()
        );
    }

    private String normalizeQuery(String q) {
        if (q == null) {
            return null;
        }
        String trimmed = q.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        if (trimmed.length() > 100) {
            throw new IllegalArgumentException("Search query must not exceed 100 characters");
        }
        return trimmed;
    }

    private String normalizeGenre(String genre) {
        if (genre == null) {
            return null;
        }
        String trimmed = genre.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("all")) {
            return null;
        }
        if (trimmed.length() > 50) {
            throw new IllegalArgumentException("Genre must not exceed 50 characters");
        }
        return trimmed;
    }

    private Pageable pageable(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("Page must be 1 or greater");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Page size must be between 1 and 100");
        }
        Sort sort = Sort.by(Sort.Order.asc("title").ignoreCase(), Sort.Order.asc("isbn"));
        return PageRequest.of(page - 1, size, sort);
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
