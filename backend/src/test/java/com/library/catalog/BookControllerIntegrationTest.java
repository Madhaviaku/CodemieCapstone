package com.library.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.catalog.dto.BookCreateRequest;
import com.library.catalog.dto.BookUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndRetrieveBook() throws Exception {
        BookCreateRequest request = new BookCreateRequest(
                "978-0-13-468599-1",
                "Effective Java",
                "Joshua Bloch",
                "Addison-Wesley",
                LocalDate.of(2018, 1, 6),
                "Programming",
                5
        );

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("Book created successfully"))
                .andExpect(jsonPath("$.data.isbn").value("9780134685991"))
                .andExpect(jsonPath("$.data.title").value("Effective Java"));

        mockMvc.perform(get("/api/books/9780134685991"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.author").value("Joshua Bloch"));

        mockMvc.perform(get("/api/books").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].isbn").exists())
                .andExpect(jsonPath("$.data.pageSize").value(20));

        mockMvc.perform(get("/api/books/search").param("title", "effective"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Search completed successfully"))
                .andExpect(jsonPath("$.data.content[0].title").value("Effective Java"));
    }

    @Test
    void updateAndDeleteBook() throws Exception {
        BookCreateRequest request = new BookCreateRequest(
                "9780201633610",
                "Design Patterns",
                "Erich Gamma",
                "Addison-Wesley",
                LocalDate.of(1994, 10, 31),
                "Software Engineering",
                4
        );
        String createdBody = mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String createdAt = objectMapper.readTree(createdBody).get("data").get("updatedAt").asText();
        Thread.sleep(30);

        BookUpdateRequest update = new BookUpdateRequest(
                "Design Patterns (reissue)",
                null,
                null,
                null,
                null,
                9
        );
        String updatedBody = mockMvc.perform(put("/api/books/9780201633610")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Design Patterns (reissue)"))
                .andExpect(jsonPath("$.data.quantityAvailable").value(9))
                .andExpect(jsonPath("$.data.isbn").value("9780201633610"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertNotEquals(createdAt, objectMapper.readTree(updatedBody).get("data").get("updatedAt").asText());

        mockMvc.perform(delete("/api/books/9780201633610"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/9780201633610"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Book with ISBN 9780201633610 not found"));
    }

    @Test
    void missingBookReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/books/0000000000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void duplicateIsbnReturnsConflict() throws Exception {
        BookCreateRequest request = new BookCreateRequest(
                "9780132350884",
                "Clean Code",
                "Robert C. Martin",
                "Prentice Hall",
                LocalDate.of(2008, 8, 1),
                "Programming",
                2
        );
        String body = objectMapper.writeValueAsString(request);
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/books").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    void invalidCreateReturnsFieldErrors() throws Exception {
        String payload = "{\"isbn\":\"abc\",\"title\":\"\",\"author\":\"\",\"genre\":\"\",\"quantityAvailable\":-1}";

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.isbn").exists())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void futurePublicationDateIsRejected() throws Exception {
        String payload = """
                {
                  "isbn": "9780262033848",
                  "title": "Introduction to Algorithms",
                  "author": "Thomas H. Cormen",
                  "publicationDate": "2099-01-01",
                  "genre": "Computer Science",
                  "quantityAvailable": 1
                }
                """;

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.publicationDate").value("Publication date cannot be in the future"));
    }

    @Test
    void filteredCatalogMatchesAuthorGenreAndYear() throws Exception {
        createBook("9780306406157", "Filtered Harbor", "Ada Filter", "Cartography", LocalDate.of(2005, 6, 1));
        createBook("9780137081073", "Later Harbor", "Ada Filter", "Cartography", LocalDate.of(2018, 1, 1));
        createBook("9780743273565", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", LocalDate.of(1925, 4, 10));

        mockMvc.perform(get("/api/books").param("q", "ada").param("genre", "cartography").param("yearMin", "2000").param("yearMax", "2010"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Search completed successfully"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Filtered Harbor"));

        mockMvc.perform(get("/api/books").param("q", "978-0-306-40615-7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("Filtered Harbor"));

        mockMvc.perform(get("/api/books").param("yearMin", "2010").param("yearMax", "2000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Min year cannot be greater than max year"));
    }

    private void createBook(String isbn, String title, String author, String genre, LocalDate published) throws Exception {
        BookCreateRequest request = new BookCreateRequest(isbn, title, author, "Publisher", published, genre, 1);
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void searchWithoutTitleIsRejected() throws Exception {
        mockMvc.perform(get("/api/books/search"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("title is required"));
    }
}
