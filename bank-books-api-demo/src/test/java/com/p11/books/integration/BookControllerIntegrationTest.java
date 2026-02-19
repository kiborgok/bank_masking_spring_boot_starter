package com.p11.books.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p11.books.dto.BookDto;
import com.p11.books.repository.BookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "p11.masking.enabled=true",
        "p11.masking.fields=email,phoneNumber",
        "p11.masking.mask-style=PARTIAL"
})
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void clearDb() {
        bookRepository.deleteAll();
    }

    // -----------------------------------------------------------------------
    // CREATE
    // -----------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("POST /api/v1/books - creates book and returns 201")
    void createBook_returns201() throws Exception {
        BookDto dto = new BookDto(null, "Clean Code", "Robert Martin",
                "robert@example.com", "+254712345678", "Prentice Hall");

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.email").value("robert@example.com"))    // DB value unmasked
                .andExpect(jsonPath("$.phoneNumber").value("+254712345678"));  // DB value unmasked
    }

    @Test
    @DisplayName("POST /api/v1/books - returns 400 for missing required fields")
    void createBook_returns400_forInvalidInput() throws Exception {
        BookDto dto = new BookDto(null, "", "", null, null, null); // blank title and author

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // -----------------------------------------------------------------------
    // READ
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("GET /api/v1/books/{id} - returns book when exists")
    void findById_returnsBook() throws Exception {
        Long id = createTestBook();

        mockMvc.perform(get("/api/v1/books/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Test Book"));
    }

    @Test
    @DisplayName("GET /api/v1/books/{id} - returns 404 for missing book")
    void findById_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/books/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/books - returns list of all books")
    void findAll_returnsBooks() throws Exception {
        createTestBook();

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // -----------------------------------------------------------------------
    // UPDATE
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("PUT /api/v1/books/{id} - updates existing book")
    void updateBook_returns200() throws Exception {
        Long id = createTestBook();
        BookDto update = new BookDto(null, "Updated Title", "New Author",
                "new@example.com", "0799999999", "New Publisher");

        mockMvc.perform(put("/api/v1/books/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @DisplayName("PUT /api/v1/books/{id} - returns 404 for missing book")
    void updateBook_returns404() throws Exception {
        BookDto dto = new BookDto(null, "T", "A", "e@t.com", "0", "P");

        mockMvc.perform(put("/api/v1/books/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // -----------------------------------------------------------------------
    // DELETE
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /api/v1/books/{id} - deletes existing book")
    void deleteBook_returns204() throws Exception {
        Long id = createTestBook();

        mockMvc.perform(delete("/api/v1/books/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/books/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/v1/books/{id} - returns 404 for missing book")
    void deleteBook_returns404() throws Exception {
        mockMvc.perform(delete("/api/v1/books/999999"))
                .andExpect(status().isNotFound());
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private Long createTestBook() throws Exception {
        BookDto dto = new BookDto(null, "Test Book", "Test Author",
                "test@example.com", "+254700000000", "Test Publisher");

        String response = mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }
}
