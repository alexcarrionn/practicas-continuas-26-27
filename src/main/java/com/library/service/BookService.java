package com.library.service;

import com.library.dto.BookRequest;
import com.library.dto.BookResponse;

import java.util.List;

public interface BookService {

    BookResponse create(BookRequest request);

    BookResponse findById(Long id);

    List<BookResponse> findAll();

    BookResponse update(Long id, BookRequest request);

    void delete(Long id);
}
