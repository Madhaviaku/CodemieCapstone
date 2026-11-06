package com.library.catalog.search;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Black-box HTTP tests for EPMCDMETST-67 enhanced multi-field search.
 *
 * Notes:
 * - Uses BASE_URL as the application origin (default http://localhost:5173).
 * - Exercises backend API endpoint mounted under the same origin: /api/books
 *   (as defined in backend BookController).
 */
public class EnhancedSearchHttpTest {

    private static final String DEFAULT_BASE_URL = "http://localhost:5173";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private String baseUrl() {
        String fromEnv = System.getenv("BASE_URL");
        return (fromEnv == null || fromEnv.isBlank()) ? DEFAULT_BASE_URL : fromEnv;
    }

    private HttpResponse<String> get(String pathAndQuery) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl() + pathAndQuery))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    private static String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    @Test
    void apiBooks_shouldReturn200_andJsonEnvelope() throws Exception {
        HttpResponse<String> res = get("/api/books?page=0&size=5");
        assertEquals(200, res.statusCode(), () -> "Body: " + res.body());
        assertTrue(res.headers().firstValue("content-type").orElse("").contains("application/json"));
        assertNotNull(res.body());
        assertTrue(res.body().contains("\"data\""), () -> "Expected ApiResponse envelope. Body: " + res.body());
    }

    @Test
    void enhancedSearch_shouldBeCaseInsensitive_forTitleMatch() throws Exception {
        // Acceptance: searching for different casing should still match.
        HttpResponse<String> res = get("/api/books?q=" + enc("the hobbit") + "&page=0&size=20");
        assertEquals(200, res.statusCode(), () -> "Body: " + res.body());

        // We assert contract-level behavior (no crash and returns JSON). If seeded data contains
        // "The Hobbit" the match should be present.
        assertTrue(res.body().toLowerCase().contains("the hobbit"),
                () -> "Expected response to include 'The Hobbit' (case-insensitive). Body: " + res.body());
    }

    @Test
    void enhancedSearch_shouldSupportPartialMatch_acrossAuthorGenreOrIsbn() throws Exception {
        // We can't rely on exact dataset here; this test validates that the endpoint accepts q
        // and returns 200 even for partial inputs.
        HttpResponse<String> res = get("/api/books?q=" + enc("fic") + "&page=0&size=20");
        assertEquals(200, res.statusCode(), () -> "Body: " + res.body());
        assertTrue(res.body().contains("\"data\""), () -> "Body: " + res.body());
    }

    @Test
    void emptyQuery_shouldReturnFullUnfilteredList_sameAsNoQ() throws Exception {
        HttpResponse<String> noQ = get("/api/books?page=0&size=20");
        HttpResponse<String> emptyQ = get("/api/books?q=" + enc("   ") + "&page=0&size=20");

        assertEquals(200, noQ.statusCode(), () -> "Body: " + noQ.body());
        assertEquals(200, emptyQ.statusCode(), () -> "Body: " + emptyQ.body());

        // Since server trims and treats blank as no filter, totalElements should match.
        // We do a lightweight check by comparing the full JSON bodies. This assumes stable sort.
        assertEquals(noQ.body(), emptyQ.body(), "Blank q should behave like no q (unfiltered)");
    }

    @Test
    void noResults_shouldReturn200_andZeroCount() throws Exception {
        HttpResponse<String> res = get("/api/books?q=" + enc("__definitely_no_such_book__") + "&page=0&size=20");
        assertEquals(200, res.statusCode(), () -> "Body: " + res.body());

        // BookPageResponse is wrapped; look for totalElements":0
        assertTrue(res.body().contains("\"totalElements\":0"),
                () -> "Expected totalElements=0 for no-match query. Body: " + res.body());
    }

    @Test
    void pagination_shouldReflectFilteredDataset_totalPagesNonIncreasingWithFilter() throws Exception {
        // Compare totalPages between unfiltered and filtered; filtered should be <= unfiltered.
        HttpResponse<String> all = get("/api/books?page=0&size=5");
        HttpResponse<String> filtered = get("/api/books?q=" + enc("the") + "&page=0&size=5");

        assertEquals(200, all.statusCode(), () -> "Body: " + all.body());
        assertEquals(200, filtered.statusCode(), () -> "Body: " + filtered.body());

        int allPages = extractInt(all.body(), "totalPages");
        int filteredPages = extractInt(filtered.body(), "totalPages");

        assertTrue(filteredPages <= allPages,
                () -> "Expected filtered totalPages <= unfiltered. all=" + allPages + " filtered=" + filteredPages);
    }

    private static int extractInt(String json, String field) {
        // Very small JSON field extractor to avoid adding dependencies.
        // Looks for: "field":<number>
        String needle = "\"" + field + "\":";
        int idx = json.indexOf(needle);
        assertTrue(idx >= 0, () -> "Field not found: " + field + " in: " + json);
        int start = idx + needle.length();
        int end = start;
        while (end < json.length() && Character.isWhitespace(json.charAt(end))) end++;
        int numStart = end;
        while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
        assertTrue(end > numStart, () -> "No digits for field " + field + " in: " + json);
        return Integer.parseInt(json.substring(numStart, end));
    }
}
