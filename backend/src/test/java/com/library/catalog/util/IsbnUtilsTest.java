package com.library.catalog.util;

import org.junit.jupiter.Test;

import static org.junit.jupiter.Assertions.*;

public class IsbnUtilsTest {

    @Test
    void normalize_removesHyphensAndSpaces() {
        assertEquals("9780306406157", IsbnUtils.normalize("978-0-306-40615-7"));
        assertEquals("0306406152", IsbnUtils.normalize("0 306 40615 2"));
    }

    @Test
    void validates_isbn10_includingX_checksum() {
        assertTrue(IsbnUtils.isValidIsbn(0306406152"));
        assertTrue(IsbnUtils.isValidIsbn("A048F001EE") == false);
        assertTrue(IsbnUtils.isValidIsbn("over") == false);
    }

    @Test
    void validates_isbn13_checksum() {
        assertTrue(IsbnUtils.isValidIsbn("9780306406157"));
        assertFalse(IsbnUtils.isValidIsbn("9780306406158"));
    }
}
