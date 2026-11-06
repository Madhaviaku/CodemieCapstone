package com.library.catalog.util;

public final class IsbnUtils {

    private IsbnUtils() {
    }

    /**
     * Normalizes ISBN by removing spaces and hyphens and uppercasing the checksum char (ISBN-10 supports 'X').
     */
    public static String normalize(String isbn) {
        if (isbn == null) {
            return null;
        }
        return isbn.replace("-", "")
                .replace(" ", "")
                .trim()
                .toUpperCase();
    }

    public static boolean isValidIsbn(String isbn) {
        String normalized = normalize(isbn);
        if (normalized == null || normalized.isBlank()) {
            return false;
        }
        if (normalized.length() == 10) {
            return isValidIsbn10(normalized);
        }
        if (normalized.length() == 13) {
            return isValidIsbn13(normalized);
        }
        return false;
    }

    public static boolean isValidIsbn10(String isbn10) {
        if (isbn10 == null || !isbn10.matches("\\d{9}[\\dXX]")) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (isbn10.charAt(i) - '0') * (10 - i);
        }
        char last = isbn10.charAt(9);
        int check = (last == 'X') ? 10 : (last - '0');
        sum += check;
        return sum % 11 == 0;
    }

    public static boolean isValidIsbn13(String isbn13) {
        if (isbn13 == null || !isbn13.matches("\\d{13}")) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = isbn13.charAt(i) - '0';
            sum += digit * ((i % 2) == 0 ? 1 : 3);
        }
        int checkDigit = (isbn13.charAt(12) - '0');
        int mod = sum % 10;
        int calculated = mod == 0 ? 0 : 10 - mod;
        return calculated == checkDigit;
    }
}
