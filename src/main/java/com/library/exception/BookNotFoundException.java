package com.library.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Libro no encontrado con ID: " + id);
    }
}
