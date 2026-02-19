package com.p11.books.integration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.p11.books.dto.BookDto;
import com.p11.books.repository.BookRepository;
import com.p11.books.service.BookService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test that verifies:
 * <ol>
 *   <li>Sensitive fields (email, phoneNumber) are MASKED in log output.</li>
 *   <li>The same fields are stored UNMASKED in the database.</li>
 * </ol>
 */
@SpringBootTest
@TestPropertySource(properties = {
        "p11.masking.enabled=true",
        "p11.masking.fields=email,phoneNumber",
        "p11.masking.mask-style=PARTIAL",
        "p11.masking.mask-character=*"
})
class MaskingIntegrationTest {

    private static final String EMAIL = "alice@example.com";
    private static final String PHONE = "+254712345678";

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger serviceLogger;

    @BeforeEach
    void attachAppender() {
        bookRepository.deleteAll();
        serviceLogger = (Logger) LoggerFactory.getLogger(BookService.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        serviceLogger.addAppender(listAppender);
        serviceLogger.setLevel(Level.INFO);
    }

    @AfterEach
    void detachAppender() {
        serviceLogger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("Logs must show masked email and phoneNumber values")
    void logsShowMaskedValues() {
        BookDto dto = new BookDto(null, "Refactoring", "Fowler", EMAIL, PHONE, "Addison");
        bookService.create(dto);

        List<String> logMessages = listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        // At least one log message from create()
        assertThat(logMessages).isNotEmpty();

        String createLog = logMessages.stream()
                .filter(msg -> msg.contains("Creating book"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No 'Creating book' log message found"));

        // Raw sensitive values MUST NOT appear in logs
        assertThat(createLog)
                .as("Email must be masked in logs")
                .doesNotContain(EMAIL);

        assertThat(createLog)
                .as("Phone number must be masked in logs")
                .doesNotContain(PHONE);

        // Masked characters must be present
        assertThat(createLog)
                .as("Masked characters should appear in log")
                .contains("*");
    }

    @Test
    @DisplayName("Database must store unmasked values")
    void databaseStoresUnmaskedValues() {
        BookDto dto = new BookDto(null, "Refactoring", "Fowler", EMAIL, PHONE, "Addison");
        BookDto saved = bookService.create(dto);

        // Re-fetch directly from DB
        var dbBook = bookRepository.findById(saved.getId()).orElseThrow();

        assertThat(dbBook.getEmail())
                .as("Database must hold original email, not masked")
                .isEqualTo(EMAIL);

        assertThat(dbBook.getPhoneNumber())
                .as("Database must hold original phone, not masked")
                .isEqualTo(PHONE);
    }

    @Test
    @DisplayName("Original DTO object is not modified after logging")
    void originalDtoIsNotModified() {
        BookDto dto = new BookDto(null, "DDD", "Evans", EMAIL, PHONE, "Prentice");
        String originalEmail = dto.getEmail();
        String originalPhone = dto.getPhoneNumber();

        bookService.create(dto);

        // The DTO object must remain unchanged after the log call
        assertThat(dto.getEmail())
                .as("DTO email must not be modified by masking")
                .isEqualTo(originalEmail);

        assertThat(dto.getPhoneNumber())
                .as("DTO phone must not be modified by masking")
                .isEqualTo(originalPhone);
    }
}
