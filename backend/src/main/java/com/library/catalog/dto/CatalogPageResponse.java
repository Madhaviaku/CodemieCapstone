package com.library.catalog.dto;

import java.util.List;

public record CatalogPageResponse(
        List<BookResponse> items,
        long totalCount,
        int page,
        int pageSize
) {
}
