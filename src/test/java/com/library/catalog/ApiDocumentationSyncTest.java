package com.library.catalog;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiDocumentationSyncTest {

    @Test
    void committedDocumentationMatchesGenerator() throws Exception {
        String expected = ApiDocumentationGenerator.generateMarkdown();
        String actual = Files.readString(Path.of("docs", "api.md")).replace("\r\n", "\n");
        assertEquals(expected, actual);
    }
}
