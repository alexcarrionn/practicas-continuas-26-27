package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.BookSearchCriteria;
import com.library.dto.BookSearchResponse;
import java.util.List;

public interface BookService {

  BookResponse create(BookRequest request);

  BookResponse findById(Long id);

  List<BookResponse> findAll();

  BookSearchResponse search(BookSearchCriteria criteria, int page, int size, String sort);

  BookResponse update(Long id, BookRequest request);

  void delete(Long id);
}
