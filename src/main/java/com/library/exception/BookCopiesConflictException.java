package com.library.exception;

public class BookCopiesConflictException extends RuntimeException {

  private BookCopiesConflictException(String message) {
    super(message);
  }

  public static BookCopiesConflictException noCopiesAvailable(Long id) {
    return new BookCopiesConflictException(
        "No hay ejemplares disponibles para prestar del libro con ID: " + id);
  }

  public static BookCopiesConflictException allCopiesReturned(Long id) {
    return new BookCopiesConflictException(
        "Todos los ejemplares del libro con ID " + id + " ya están devueltos");
  }
}
