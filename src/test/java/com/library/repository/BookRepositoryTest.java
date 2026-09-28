package com.library.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.library.model.Book;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@DataJpaTest
class BookRepositoryTest {

  @Autowired private BookRepository bookRepository;

  @BeforeEach
  void setUp() {
    bookRepository.save(book("Disponible", 2, 1));
    bookRepository.save(book("Agotado", 1, 0));
  }

  @Test
  void search_withAvailableTrue_returnsOnlyBooksWithAvailableCopies() {
    assertEquals(List.of("Disponible"), searchTitles(true));
  }

  @Test
  void search_withAvailableFalse_returnsOnlyBooksWithoutAvailableCopies() {
    assertEquals(List.of("Agotado"), searchTitles(false));
  }

  @Test
  void search_withoutAvailable_returnsAllBooks() {
    assertEquals(List.of("Agotado", "Disponible"), searchTitles(null));
  }

  private List<String> searchTitles(Boolean available) {
    return bookRepository
        .search(null, null, null, null, null, available, PageRequest.of(0, 10, Sort.by("title")))
        .map(Book::getTitle)
        .getContent();
  }

  private static Book book(String title, int copies, int availableCopies) {
    return Book.builder()
        .title(title)
        .author("Autor")
        .genre("Novela")
        .copies(copies)
        .availableCopies(availableCopies)
        .build();
  }
}
