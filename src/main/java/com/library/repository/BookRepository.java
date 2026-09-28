package com.library.repository;

import com.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookRepository extends JpaRepository<Book, Long> {

  boolean existsByIsbn(String isbn);

  boolean existsByIsbnAndIdNot(String isbn, Long id);

  @Query(
      """
            select b from Book b
            where (:keyword is null
                or lower(b.title) like lower(concat('%', :keyword, '%'))
                or lower(b.author) like lower(concat('%', :keyword, '%')))
              and (:author is null
                or lower(b.author) like lower(concat('%', :author, '%')))
              and (:genre is null
                or lower(b.genre) like lower(concat('%', :genre, '%')))
              and (:minYear is null or b.publishedYear >= :minYear)
              and (:maxYear is null or b.publishedYear <= :maxYear)
              and (:available is null
                or (:available = true and b.availableCopies > 0)
                or (:available = false and b.availableCopies = 0))
            """)
  Page<Book> search(
      @Param("keyword") String keyword,
      @Param("author") String author,
      @Param("genre") String genre,
      @Param("minYear") Integer minYear,
      @Param("maxYear") Integer maxYear,
      @Param("available") Boolean available,
      Pageable pageable);
}
