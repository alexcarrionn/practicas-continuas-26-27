package com.library.mapper;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

  private static final int DEFAULT_COPIES = 1;

  public Book toEntity(BookRequest request) {
    int copies = request.copies() != null ? request.copies() : DEFAULT_COPIES;
    return Book.builder()
        .title(request.title().trim())
        .author(request.author().trim())
        .genre(request.genre().trim())
        .isbn(request.isbn())
        .publishedYear(request.publishedYear())
        .pages(request.pages())
        .copies(copies)
        .availableCopies(copies)
        .build();
  }

  public BookResponse toResponse(Book book) {
    return new BookResponse(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getGenre(),
        book.getIsbn(),
        book.getPublishedYear(),
        book.getPages(),
        book.getCopies(),
        book.getAvailableCopies());
  }
}
