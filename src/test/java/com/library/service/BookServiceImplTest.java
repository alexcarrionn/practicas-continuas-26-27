package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateBookException;
import com.library.exception.InvalidBookException;
import com.library.mapper.BookMapper;
import com.library.model.Book;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

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
        BookRequest request = new BookRequest("Dune", "Frank Herbert", "Ciencia ficción",
                "9780441013593", Year.now().getValue() + 1, 500);

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
        BookRequest request = new BookRequest("Mil novecientos ochenta y cuatro", "George Orwell",
                "Distopía", "9780451524935", 1949, 350);
        BookResponse response = new BookResponse(1L, "Mil novecientos ochenta y cuatro", "George Orwell",
                "Distopía", "9780451524935", 1949, 350);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByIsbnAndIdNot(request.isbn(), 1L)).thenReturn(false);
        when(bookRepository.save(existing)).thenReturn(existing);
        when(bookMapper.toResponse(existing)).thenReturn(response);

        assertEquals(response, bookService.update(1L, request));
        assertEquals(350, existing.getPages());
        verify(bookRepository).save(existing);
    }

    @Test
    void findById_withMissingBook_throwsNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.findById(99L));
    }

    @Test
    void update_withMissingBook_throwsNotFoundAndDoesNotSave() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class,
                () -> bookService.update(99L, validRequest(null)));
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

    private static BookRequest validRequest(String isbn) {
        return new BookRequest("1984", "George Orwell", "Distopía", isbn, 1949, 328);
    }

    private static Book book(Long id, String title, String author, String genre, String isbn, int year, int pages) {
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
