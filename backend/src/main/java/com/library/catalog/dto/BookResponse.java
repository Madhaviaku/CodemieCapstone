package com.library.catalog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        String publisher,
        LocalDate publicationDate,
        String genre,
        Integer quantityAvailable,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
