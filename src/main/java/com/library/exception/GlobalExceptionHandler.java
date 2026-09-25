package com.library.exception;

import com.library.dto.ErrorResponse;
import com.library.dto.ValidationErrorResponse;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BookNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("BOOK_NOT_FOUND", ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(InvalidBookException.class)
  public ResponseEntity<ErrorResponse> handleInvalidBook(InvalidBookException ex) {
    return ResponseEntity.badRequest()
        .body(new ErrorResponse("INVALID_BOOK", ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(DuplicateBookException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateBook(DuplicateBookException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(new ErrorResponse("DUPLICATE_BOOK", ex.getMessage(), LocalDateTime.now()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex) {

    Map<String, String> fieldErrors =
        ex.getBindingResult().getFieldErrors().stream()
            .collect(
                Collectors.toMap(
                    FieldError::getField,
                    error ->
                        error.getDefaultMessage() != null
                            ? error.getDefaultMessage()
                            : "Error de validación",
                    (first, second) -> first));

    return ResponseEntity.badRequest()
        .body(
            new ValidationErrorResponse(
                "VALIDATION_ERROR",
                "Los datos de entrada contienen errores de validación",
                fieldErrors,
                LocalDateTime.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(
                "INTERNAL_ERROR",
                "Ha ocurrido un error interno del servidor",
                LocalDateTime.now()));
  }
}
