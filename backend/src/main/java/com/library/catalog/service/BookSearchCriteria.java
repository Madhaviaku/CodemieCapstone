package com.library.catalog.service;

public record BookSearchCriteria(
        String q,
        String genre,
        Integer year,
        Boolean availableOnly
) {
}
