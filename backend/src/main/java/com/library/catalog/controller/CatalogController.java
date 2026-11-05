package com.library.catalog.controller;

import com.library.catalog.dto.ApiResponse;
import com.library.catalog.dto.CatalogPageResponse;
import com.library.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/catalog")
    public ResponseEntity<ApiResponse<CatalogPageResponse>> getCatalog(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String genre,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                200,
                "Catalog retrieved successfully",
                catalogService.getCatalog(q, genre, page, pageSize)
        ));
    }
}
