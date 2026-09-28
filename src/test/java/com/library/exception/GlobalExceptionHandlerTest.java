package com.library.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.library.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void missingBookIsTranslatedToHttp404() {
    ResponseEntity<ErrorResponse> response =
        handler.handleBookNotFound(new BookNotFoundException(99L));

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("BOOK_NOT_FOUND", response.getBody().error());
    assertEquals("Libro no encontrado con ID: 99", response.getBody().message());
  }

  @Test
  void invalidBookIsTranslatedToHttp400() {
    ResponseEntity<ErrorResponse> response =
        handler.handleInvalidBook(
            new InvalidBookException("El año de publicación no puede ser futuro"));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("INVALID_BOOK", response.getBody().error());
  }

  @Test
  void noCopiesAvailableIsTranslatedToHttp409() {
    ResponseEntity<ErrorResponse> response =
        handler.handleBookCopiesConflict(BookCopiesConflictException.noCopiesAvailable(5L));

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("BOOK_COPIES_CONFLICT", response.getBody().error());
    assertEquals(
        "No hay ejemplares disponibles para prestar del libro con ID: 5",
        response.getBody().message());
  }

  @Test
  void allCopiesReturnedIsTranslatedToHttp409() {
    ResponseEntity<ErrorResponse> response =
        handler.handleBookCopiesConflict(BookCopiesConflictException.allCopiesReturned(5L));

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("BOOK_COPIES_CONFLICT", response.getBody().error());
    assertEquals(
        "Todos los ejemplares del libro con ID 5 ya están devueltos", response.getBody().message());
  }
}
