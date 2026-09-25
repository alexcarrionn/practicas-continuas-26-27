package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.BookSearchCriteria;
import com.library.dto.BookSearchResponse;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateBookException;
import com.library.exception.InvalidBookException;
import com.library.mapper.BookMapper;
import com.library.model.Book;
import com.library.repository.BookRepository;
import java.time.Year;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookServiceImpl implements BookService {

  private static final int MAX_SEARCH_PAGE_SIZE = 100;
  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of("title", "author", "genre", "publishedYear", "pages");

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
    return bookRepository
        .findById(id)
        .map(bookMapper::toResponse)
        .orElseThrow(() -> new BookNotFoundException(id));
  }

  @Override
  @Transactional(readOnly = true)
  public List<BookResponse> findAll() {
    return bookRepository.findAll().stream().map(bookMapper::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public BookSearchResponse search(BookSearchCriteria criteria, int page, int size, String sort) {
    if (criteria.minYear() != null
        && criteria.maxYear() != null
        && criteria.minYear() > criteria.maxYear()) {
      throw new InvalidBookException("El año mínimo no puede ser mayor que el año máximo");
    }

    if (page < 0) {
      throw new InvalidBookException("El número de página no puede ser negativo");
    }

    if (size < 1 || size > MAX_SEARCH_PAGE_SIZE) {
      throw new InvalidBookException(
          "El tamaño de página debe estar entre 1 y " + MAX_SEARCH_PAGE_SIZE);
    }

    Pageable pageable = buildSearchPageable(page, size, sort);

    Page<BookResponse> result =
        bookRepository
            .search(
                normalize(criteria.keyword()),
                normalize(criteria.author()),
                normalize(criteria.genre()),
                criteria.minYear(),
                criteria.maxYear(),
                pageable)
            .map(bookMapper::toResponse);

    return BookSearchResponse.from(result);
  }

  @Override
  public BookResponse update(Long id, BookRequest request) {
    Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));

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

  private Pageable buildSearchPageable(int page, int size, String sort) {
    String normalizedSort = sort == null || sort.isBlank() ? "title,asc" : sort.trim();
    String[] parts = normalizedSort.split(",", -1);

    if (parts.length > 2 || parts[0].isBlank()) {
      throw new InvalidBookException("El orden debe seguir el formato campo,dirección");
    }

    String field = parts[0].trim();
    if (!ALLOWED_SORT_FIELDS.contains(field)) {
      throw new InvalidBookException("No se permite ordenar por: " + field);
    }

    String direction = parts.length == 1 ? "asc" : parts[1].trim().toLowerCase();
    Sort.Direction sortDirection;
    try {
      sortDirection = Sort.Direction.fromString(direction);
    } catch (IllegalArgumentException ex) {
      throw new InvalidBookException("La dirección de orden debe ser asc o desc");
    }

    return PageRequest.of(page, size, Sort.by(sortDirection, field));
  }

  private String normalize(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return value.trim();
  }
}
