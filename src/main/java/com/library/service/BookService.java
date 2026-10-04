package com.library.service;

import java.util.List;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;
import com.library.dto.BookSearchCriteria;
import com.library.dto.BookSearchResponse;
import com.library.dto.RatingRequest;
import com.library.dto.RatingResponse;
import com.library.dto.BookStatsResponse;
import java.util.List;

public interface BookService {

  BookResponse create(BookRequest request);

  BookResponse findById(Long id);

  List<BookResponse> findAll();

  BookSearchResponse search(BookSearchCriteria criteria, int page, int size, String sort);

  BookStatsResponse getStats();

  BookResponse update(Long id, BookRequest request);

  void delete(Long id);

  BookResponse borrow(Long id);

  BookResponse returnBook(Long id);
  
  RatingResponse addRating(Long id, RatingRequest request);

  RatingResponse getRating(Long id);
}
