package com.library.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.model.Book;
import org.junit.jupiter.api.Test;

class BookMapperTest {

  private final BookMapper bookMapper = new BookMapper();

  @Test
  void toEntity_withoutCopies_defaultsToOneAvailableCopy() {
    BookRequest request = new BookRequest("Libro", "Autor", "Novela", null, 2000, 100, null);

    Book book = bookMapper.toEntity(request);

    assertEquals(1, book.getCopies());
    assertEquals(1, book.getAvailableCopies());
  }

  @Test
  void toEntity_withCopies_makesAllCopiesAvailable() {
    BookRequest request = new BookRequest("Libro", "Autor", "Novela", null, 2000, 100, 3);

    Book book = bookMapper.toEntity(request);

    assertEquals(3, book.getCopies());
    assertEquals(3, book.getAvailableCopies());
  }

  @Test
  void toResponse_includesCopiesAndAvailableCopies() {
    Book book = Book.builder().id(1L).title("Libro").copies(3).availableCopies(2).build();

    BookResponse response = bookMapper.toResponse(book);

    assertEquals(3, response.copies());
    assertEquals(2, response.availableCopies());
  }
}
