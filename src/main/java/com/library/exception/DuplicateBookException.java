package com.library.exception;

public class DuplicateBookException extends RuntimeException {

  public DuplicateBookException(String isbn) {
    super("Ya existe un libro con el ISBN: " + isbn);
  }
}
