package com.library.catalog.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record BookPageResponse(
        List<BookResponse> content,
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize
) {
    public static BookPageResponse from(Page<BookResponse> page) {
        return new BookPageResponse(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }
}
