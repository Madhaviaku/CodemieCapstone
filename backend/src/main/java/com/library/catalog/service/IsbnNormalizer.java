package com.library.catalog.service;

public final class IsbnNormalizer {
    private IsbnNormalizer() {
    }

    public static String normalizeIsbnInput(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        // Matches arch doc: strip spaces and hyphens for lookup.
        return trimmed.replaceAll("[\\s-]", "");
    }
}
