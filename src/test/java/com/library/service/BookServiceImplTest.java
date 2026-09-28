package com.library.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.BookSearchCriteria;
import com.library.dto.BookSearchResponse;
import com.library.exception.BookCopiesConflictException;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateBookException;
import com.library.exception.InvalidBookException;
import com.library.mapper.BookMapper;
import com.library.model.Book;
import com.library.repository.BookRepository;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

  @Mock private BookRepository bookRepository;

  @Mock private BookMapper bookMapper;

  @InjectMocks private BookServiceImpl bookService;

  @Test
  void create_withExistingIsbn_rejectsDuplicateAndDoesNotSave() {
    BookRequest request = validRequest("9780451524935");
    when(bookRepository.existsByIsbn(request.isbn())).thenReturn(true);

    assertThrows(DuplicateBookException.class, () -> bookService.create(request));
    verify(bookRepository, never()).save(org.mockito.ArgumentMatchers.any());
    verifyNoInteractions(bookMapper);
  }

  @Test
  void create_withFuturePublicationYear_rejectsBook() {
    BookRequest request =
        new BookRequest(
            "Dune",
            "Frank Herbert",
            "Ciencia ficción",
            "9780441013593",
            Year.now().getValue() + 1,
            500,
            null);

    assertThrows(InvalidBookException.class, () -> bookService.create(request));
    verifyNoInteractions(bookRepository, bookMapper);
  }

  @Test
  void update_withIsbnBelongingToAnotherBook_rejectsDuplicate() {
    Book existing = book(1L, "1984", "George Orwell", "Distopía", "9780451524935", 1949, 328);
    BookRequest request = validRequest("9780441013593");

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(bookRepository.existsByIsbnAndIdNot(request.isbn(), 1L)).thenReturn(true);

    assertThrows(DuplicateBookException.class, () -> bookService.update(1L, request));
    verify(bookRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void update_withSameIsbn_isAllowedAndPersistsChanges() {
    Book existing = book(1L, "1984", "George Orwell", "Distopía", "9780451524935", 1949, 328);
    BookRequest request =
        new BookRequest(
            "Mil novecientos ochenta y cuatro",
            "George Orwell",
            "Distopía",
            "9780451524935",
            1949,
            350,
            null);
    BookResponse response =
        new BookResponse(
            1L,
            "Mil novecientos ochenta y cuatro",
            "George Orwell",
            "Distopía",
            "9780451524935",
            1949,
            350,
            1,
            1);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(bookRepository.existsByIsbnAndIdNot(request.isbn(), 1L)).thenReturn(false);
    when(bookRepository.save(existing)).thenReturn(existing);
    when(bookMapper.toResponse(existing)).thenReturn(response);

    assertEquals(response, bookService.update(1L, request));
    assertEquals(350, existing.getPages());
    verify(bookRepository).save(existing);
  }

  @Test
  void update_withMoreCopies_keepsBorrowedCopies() {
    Book existing = book(1L, "1984", "George Orwell", "Distopía", "9780451524935", 1949, 328);
    existing.setCopies(3);
    existing.setAvailableCopies(1);
    BookRequest request =
        new BookRequest("1984", "George Orwell", "Distopía", "9780451524935", 1949, 328, 5);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(bookRepository.save(existing)).thenReturn(existing);

    bookService.update(1L, request);

    assertEquals(5, existing.getCopies());
    assertEquals(3, existing.getAvailableCopies());
  }

  @Test
  void update_withFewerCopiesThanBorrowed_rejectsBook() {
    Book existing = book(1L, "1984", "George Orwell", "Distopía", "9780451524935", 1949, 328);
    existing.setCopies(3);
    existing.setAvailableCopies(0);
    BookRequest request =
        new BookRequest("1984", "George Orwell", "Distopía", "9780451524935", 1949, 328, 2);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));

    assertThrows(InvalidBookException.class, () -> bookService.update(1L, request));
    verify(bookRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void update_withoutCopies_keepsCurrentCopies() {
    Book existing = book(1L, "1984", "George Orwell", "Distopía", "9780451524935", 1949, 328);
    existing.setCopies(4);
    existing.setAvailableCopies(2);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(bookRepository.save(existing)).thenReturn(existing);

    bookService.update(1L, validRequest("9780451524935"));

    assertEquals(4, existing.getCopies());
    assertEquals(2, existing.getAvailableCopies());
  }

  @Test
  void findById_withMissingBook_throwsNotFound() {
    when(bookRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.findById(99L));
  }

  @Test
  void update_withMissingBook_throwsNotFoundAndDoesNotSave() {
    when(bookRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.update(99L, validRequest(null)));
    verify(bookRepository, never()).save(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void delete_withMissingBook_throwsNotFoundAndDoesNotDelete() {
    when(bookRepository.existsById(99L)).thenReturn(false);

    assertThrows(BookNotFoundException.class, () -> bookService.delete(99L));
    verify(bookRepository, never()).deleteById(99L);
  }

  @Test
  void delete_withExistingBook_removesIt() {
    when(bookRepository.existsById(7L)).thenReturn(true);

    bookService.delete(7L);

    verify(bookRepository).deleteById(7L);
  }

  @Test
  void search_appliesFiltersPaginationAndMapsResults() {
    Book book = book(1L, "1984", "George Orwell", "Distopía", "9780451524935", 1949, 328);
    BookResponse response =
        new BookResponse(1L, "1984", "George Orwell", "Distopía", "9780451524935", 1949, 328, 1, 1);
    BookSearchCriteria criteria =
        new BookSearchCriteria(" orwell ", " George ", " Distopía ", 1900, 2000, null);

    when(bookRepository.search(
            eq("orwell"),
            eq("George"),
            eq("Distopía"),
            eq(1900),
            eq(2000),
            eq(null),
            any(Pageable.class)))
        .thenReturn(
            new PageImpl<>(
                List.of(book), PageRequest.of(2, 10, Sort.by(Sort.Direction.DESC, "pages")), 30));
    when(bookMapper.toResponse(book)).thenReturn(response);

    BookSearchResponse result = bookService.search(criteria, 2, 10, "pages,desc");

    assertEquals(30, result.totalElements());
    assertEquals(response, result.content().getFirst());
    assertEquals(2, result.page());
    assertEquals(10, result.size());
    assertEquals(3, result.totalPages());
    assertFalse(result.first());
    assertTrue(result.last());

    org.mockito.ArgumentCaptor<Pageable> pageableCaptor =
        org.mockito.ArgumentCaptor.forClass(Pageable.class);
    verify(bookRepository)
        .search(
            eq("orwell"),
            eq("George"),
            eq("Distopía"),
            eq(1900),
            eq(2000),
            eq(null),
            pageableCaptor.capture());
    Pageable pageable = pageableCaptor.getValue();
    assertEquals(2, pageable.getPageNumber());
    assertEquals(10, pageable.getPageSize());
    assertEquals("pages: DESC", pageable.getSort().toString());
  }

  @Test
  void search_withBlankFiltersUsesNullAndDefaultSort() {
    BookSearchCriteria criteria = new BookSearchCriteria("  ", "", "   ", null, null, null);
    when(bookRepository.search(
            eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    bookService.search(criteria, 0, 20, "");

    org.mockito.ArgumentCaptor<Pageable> pageableCaptor =
        org.mockito.ArgumentCaptor.forClass(Pageable.class);
    verify(bookRepository)
        .search(
            eq(null), eq(null), eq(null), eq(null), eq(null), eq(null), pageableCaptor.capture());
    assertEquals("title: ASC", pageableCaptor.getValue().getSort().toString());
  }

  @Test
  void search_withAvailableFilter_passesItToRepository() {
    BookSearchCriteria criteria = new BookSearchCriteria(null, null, null, null, null, true);
    when(bookRepository.search(
            eq(null), eq(null), eq(null), eq(null), eq(null), eq(true), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of()));

    bookService.search(criteria, 0, 20, "title,asc");

    verify(bookRepository)
        .search(eq(null), eq(null), eq(null), eq(null), eq(null), eq(true), any(Pageable.class));
  }

  @Test
  void search_withInvalidYearRange_rejectsRequestWithoutQuery() {
    BookSearchCriteria criteria = new BookSearchCriteria(null, null, null, 2020, 2010, null);

    assertThrows(
        InvalidBookException.class, () -> bookService.search(criteria, 0, 20, "title,asc"));

    verifyNoInteractions(bookRepository, bookMapper);
  }

  @Test
  void search_withUnsupportedSort_rejectsRequestWithoutQuery() {
    BookSearchCriteria criteria = new BookSearchCriteria(null, null, null, null, null, null);

    assertThrows(InvalidBookException.class, () -> bookService.search(criteria, 0, 20, "isbn,asc"));

    verifyNoInteractions(bookRepository, bookMapper);
  }

  @Test
  void search_withOversizedPage_rejectsRequestWithoutQuery() {
    BookSearchCriteria criteria = new BookSearchCriteria(null, null, null, null, null, null);

    assertThrows(
        InvalidBookException.class, () -> bookService.search(criteria, 0, 101, "title,asc"));

    verify(bookRepository, never()).search(any(), any(), any(), any(), any(), any(), any());
  }

  @Test
  void borrow_withAvailableCopies_decrementsAvailableCopies() {
    Book existing = bookWithCopies(3, 2);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(bookRepository.save(existing)).thenReturn(existing);

    bookService.borrow(1L);

    assertEquals(1, existing.getAvailableCopies());
    assertEquals(3, existing.getCopies());
    verify(bookRepository).save(existing);
  }

  @Test
  void borrow_withoutAvailableCopies_throwsConflictAndDoesNotSave() {
    when(bookRepository.findById(1L)).thenReturn(Optional.of(bookWithCopies(2, 0)));

    assertThrows(BookCopiesConflictException.class, () -> bookService.borrow(1L));
    verify(bookRepository, never()).save(any());
  }

  @Test
  void borrow_withMissingBook_throwsNotFound() {
    when(bookRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.borrow(99L));
    verify(bookRepository, never()).save(any());
  }

  @Test
  void returnBook_withBorrowedCopies_incrementsAvailableCopies() {
    Book existing = bookWithCopies(3, 1);

    when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(bookRepository.save(existing)).thenReturn(existing);

    bookService.returnBook(1L);

    assertEquals(2, existing.getAvailableCopies());
    assertEquals(3, existing.getCopies());
    verify(bookRepository).save(existing);
  }

  @Test
  void returnBook_withAllCopiesReturned_throwsConflictAndDoesNotSave() {
    when(bookRepository.findById(1L)).thenReturn(Optional.of(bookWithCopies(2, 2)));

    assertThrows(BookCopiesConflictException.class, () -> bookService.returnBook(1L));
    verify(bookRepository, never()).save(any());
  }

  @Test
  void returnBook_withMissingBook_throwsNotFound() {
    when(bookRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(BookNotFoundException.class, () -> bookService.returnBook(99L));
    verify(bookRepository, never()).save(any());
  }

  private static Book bookWithCopies(int copies, int availableCopies) {
    Book book = book(1L, "1984", "George Orwell", "Distopía", "9780451524935", 1949, 328);
    book.setCopies(copies);
    book.setAvailableCopies(availableCopies);
    return book;
  }

  private static BookRequest validRequest(String isbn) {
    return new BookRequest("1984", "George Orwell", "Distopía", isbn, 1949, 328, null);
  }

  private static Book book(
      Long id, String title, String author, String genre, String isbn, int year, int pages) {
    return Book.builder()
        .id(id)
        .title(title)
        .author(author)
        .genre(genre)
        .isbn(isbn)
        .publishedYear(year)
        .pages(pages)
        .build();
  }
}
