package com.library.controller;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.BookSearchCriteria;
import com.library.dto.BookSearchResponse;
import com.library.dto.BookStatsResponse;
import com.library.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

  private final BookService bookService;

  public BookController(BookService bookService) {
    this.bookService = bookService;
  }

  @PostMapping
  public ResponseEntity<BookResponse> create(@RequestBody @Valid BookRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(request));
  }

  @GetMapping
  public ResponseEntity<List<BookResponse>> findAll() {
    return ResponseEntity.ok(bookService.findAll());
  }

  @GetMapping("/stats")
  public ResponseEntity<BookStatsResponse> getStats() {
    return ResponseEntity.ok(bookService.getStats());
  }

  @GetMapping("/search")
  public ResponseEntity<BookSearchResponse> search(
      @RequestParam(name = "keyword", required = false) String keyword,
      @RequestParam(name = "author", required = false) String author,
      @RequestParam(name = "genre", required = false) String genre,
      @RequestParam(name = "minYear", required = false) Integer minYear,
      @RequestParam(name = "maxYear", required = false) Integer maxYear,
      @RequestParam(name = "page", defaultValue = "0") int page,
      @RequestParam(name = "size", defaultValue = "20") int size,
      @RequestParam(name = "sort", defaultValue = "title,asc") String sort) {

    BookSearchCriteria criteria = new BookSearchCriteria(keyword, author, genre, minYear, maxYear);
    return ResponseEntity.ok(bookService.search(criteria, page, size, sort));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BookResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(bookService.findById(id));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BookResponse> update(
      @PathVariable Long id, @RequestBody @Valid BookRequest request) {
    return ResponseEntity.ok(bookService.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    bookService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
