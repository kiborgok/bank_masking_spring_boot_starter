package com.p11.books.service;

import com.p11.books.dto.BookDto;
import com.p11.books.entity.Book;
import com.p11.books.exception.BookNotFoundException;
import com.p11.books.mapper.BookMapper;
import com.p11.books.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    public BookDto create(BookDto bookDto) {
        log.info("Creating book: {}", bookDto);
        Book book = bookMapper.toEntity(bookDto);
        Book saved = bookRepository.save(book);
        BookDto result = bookMapper.toDto(saved);
        log.info("Book created with id: {}", saved.getId());
        return result;
    }

    @Transactional(readOnly = true)
    public BookDto findById(Long id) {
        log.debug("Fetching book with id: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return bookMapper.toDto(book);
    }

    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        log.debug("Fetching all books");
        return bookRepository.findAll().stream()
                .map(bookMapper::toDto)
                .toList();
    }

    public BookDto update(Long id, BookDto bookDto) {
        log.info("Updating book id={}: {}", id, bookDto);   // <-- masked in logs
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookMapper.updateEntity(book, bookDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    public void delete(Long id) {
        log.info("Deleting book with id: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }
}
