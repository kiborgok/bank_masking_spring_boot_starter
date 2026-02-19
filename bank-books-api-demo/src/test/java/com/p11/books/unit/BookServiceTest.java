package com.p11.books.unit;

import com.p11.books.dto.BookDto;
import com.p11.books.entity.Book;
import com.p11.books.exception.BookNotFoundException;
import com.p11.books.mapper.BookMapper;
import com.p11.books.repository.BookRepository;
import com.p11.books.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookService")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookMapper bookMapper;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookMapper = new BookMapper();
        bookService = new BookService(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("create() saves book and returns DTO")
    void create_savesAndReturnsDto() {
        BookDto dto = new BookDto(null, "Clean Code", "Robert", "r@test.com", "0700", "Prentice");
        Book saved = new Book("Clean Code", "Robert", "r@test.com", "0700", "Prentice");
        saved.setId(1L);

        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookDto result = bookService.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Clean Code");
        assertThat(result.getEmail()).isEqualTo("r@test.com");  // raw in DB/DTO
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("findById() returns DTO when found")
    void findById_returnsDto_whenFound() {
        Book book = new Book("TDD", "Kent Beck", "kent@test.com", "0711", "Addison");
        book.setId(5L);
        when(bookRepository.findById(5L)).thenReturn(Optional.of(book));

        BookDto result = bookService.findById(5L);

        assertThat(result.getTitle()).isEqualTo("TDD");
        assertThat(result.getId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("findById() throws BookNotFoundException when not found")
    void findById_throwsNotFoundException_whenMissing() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.findById(99L))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findAll() returns all books")
    void findAll_returnsAllBooks() {
        when(bookRepository.findAll()).thenReturn(List.of(
                new Book("A", "Au1", "a@t.com", "1", "P1"),
                new Book("B", "Au2", "b@t.com", "2", "P2")
        ));

        List<BookDto> result = bookService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("update() modifies and saves book")
    void update_modifiesBook() {
        Book existing = new Book("Old", "Author", "old@t.com", "000", "Old");
        existing.setId(1L);
        BookDto dto = new BookDto(null, "New", "Author", "new@t.com", "111", "New");

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenReturn(existing);

        BookDto result = bookService.update(1L, dto);

        assertThat(result.getTitle()).isEqualTo("New");
    }

    @Test
    @DisplayName("update() throws BookNotFoundException when book missing")
    void update_throwsNotFoundException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        BookDto dto = new BookDto(null, "T", "A", "e@t.com", "0", "P");

        assertThatThrownBy(() -> bookService.update(1L, dto))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    @DisplayName("delete() calls deleteById")
    void delete_callsDeleteById() {
        when(bookRepository.existsById(1L)).thenReturn(true);
        bookService.delete(1L);
        verify(bookRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete() throws BookNotFoundException when missing")
    void delete_throwsNotFoundException() {
        when(bookRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(BookNotFoundException.class);
    }
}
