package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateBookException;
import com.library.exception.InvalidBookException;
import com.library.mapper.BookMapper;
import com.library.model.Book;
import com.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookResponse create(BookRequest request) {
        validateBusinessRules(request);
        ensureUniqueIsbn(request.isbn());
        Book saved = bookRepository.save(bookMapper.toEntity(request));
        return bookMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse findById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toResponse)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return bookRepository.findAll()
                .stream()
                .map(bookMapper::toResponse)
                .toList();
    }

    @Override
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        validateBusinessRules(request);
        ensureUniqueIsbnForUpdate(request.isbn(), id);

        book.setTitle(request.title().trim());
        book.setAuthor(request.author().trim());
        book.setGenre(request.genre().trim());
        book.setIsbn(request.isbn());
        book.setPublishedYear(request.publishedYear());
        book.setPages(request.pages());

        return bookMapper.toResponse(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }
        bookRepository.deleteById(id);
    }

    private void ensureUniqueIsbn(String isbn) {
        if (isbn != null && !isbn.isBlank() && bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateBookException(isbn);
        }
    }

    private void ensureUniqueIsbnForUpdate(String isbn, Long id) {
        if (isbn != null && !isbn.isBlank() && bookRepository.existsByIsbnAndIdNot(isbn, id)) {
            throw new DuplicateBookException(isbn);
        }
    }

    private void validateBusinessRules(BookRequest request) {
        if (request.publishedYear() != null && request.publishedYear() > Year.now().getValue()) {
            throw new InvalidBookException("El año de publicación no puede ser futuro");
        }
    }
}
