package com.library.catalog;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ApiDocumentationSyncVerifier {

    public static void main(String[] args) throws Exception {
        String expected = ApiDocumentationGenerator.generateMarkdown();
        Path docsPath = ApiDocumentationGenerator.apiDocsPath();
        if (!Files.exists(docsPath)) {
            throw new IllegalStateException(
                    "ERROR: API documentation is out of sync. Run 'mvn -q compile exec:java@generate-api-docs' to regenerate."
            );
        }

        String actual = Files.readString(docsPath, StandardCharsets.UTF_8).replace("\r\n", "\n");
        if (!expected.equals(actual)) {
            throw new IllegalStateException(
                    "ERROR: API documentation is out of sync. Run 'mvn -q compile exec:java@generate-api-docs' to regenerate."
            );
        }

        System.out.println("API documentation is in sync.");
    }
}
