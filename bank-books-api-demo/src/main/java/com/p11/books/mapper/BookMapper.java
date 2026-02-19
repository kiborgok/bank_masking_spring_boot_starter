package com.p11.books.mapper;

import com.p11.books.dto.BookDto;
import com.p11.books.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public BookDto toDto(Book book) {
        if (book == null) return null;
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getEmail(),
                book.getPhoneNumber(),
                book.getPublisher()
        );
    }

    public Book toEntity(BookDto dto) {
        if (dto == null) return null;
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setEmail(dto.getEmail());
        book.setPhoneNumber(dto.getPhoneNumber());
        book.setPublisher(dto.getPublisher());
        return book;
    }

    public void updateEntity(Book book, BookDto dto) {
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setEmail(dto.getEmail());
        book.setPhoneNumber(dto.getPhoneNumber());
        book.setPublisher(dto.getPublisher());
    }
}
