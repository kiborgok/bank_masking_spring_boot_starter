package com.p11.books.unit;

import com.p11.books.dto.BookDto;
import com.p11.books.entity.Book;
import com.p11.books.mapper.BookMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookMapperTest {

    private BookMapper bookMapper;

    @BeforeEach
    void setUp() {
        bookMapper = new BookMapper();
    }

    @Test
    void toDto_mapsAllFields() {
        Book book = new Book("Clean Code", "Robert Martin", "robert@example.com", "+254712345678", "Prentice Hall");
        book.setId(1L);

        BookDto dto = bookMapper.toDto(book);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Clean Code");
        assertThat(dto.getAuthor()).isEqualTo("Robert Martin");
        assertThat(dto.getEmail()).isEqualTo("robert@example.com");
        assertThat(dto.getPhoneNumber()).isEqualTo("+254712345678");
        assertThat(dto.getPublisher()).isEqualTo("Prentice Hall");
    }

    @Test
    void toDto_returnsNullForNullInput() {
        assertThat(bookMapper.toDto(null)).isNull();
    }

    @Test
    void toEntity_mapsAllFields() {
        BookDto dto = new BookDto(null, "SICP", "Harold Abelson", "harold@mit.edu", null, "MIT Press");

        Book book = bookMapper.toEntity(dto);

        assertThat(book.getTitle()).isEqualTo("SICP");
        assertThat(book.getAuthor()).isEqualTo("Harold Abelson");
        assertThat(book.getEmail()).isEqualTo("harold@mit.edu");
        assertThat(book.getPhoneNumber()).isNull();
        assertThat(book.getPublisher()).isEqualTo("MIT Press");
        assertThat(book.getId()).isNull();  // ID not set from DTO
    }

    @Test
    void toEntity_returnsNullForNullInput() {
        assertThat(bookMapper.toEntity(null)).isNull();
    }

    @Test
    void updateEntity_updatesAllFields() {
        Book book = new Book("Old Title", "Old Author", "old@email.com", "000", "Old Publisher");
        BookDto dto = new BookDto(null, "New Title", "New Author", "new@email.com", "111", "New Publisher");

        bookMapper.updateEntity(book, dto);

        assertThat(book.getTitle()).isEqualTo("New Title");
        assertThat(book.getAuthor()).isEqualTo("New Author");
        assertThat(book.getEmail()).isEqualTo("new@email.com");
        assertThat(book.getPhoneNumber()).isEqualTo("111");
        assertThat(book.getPublisher()).isEqualTo("New Publisher");
    }
}
