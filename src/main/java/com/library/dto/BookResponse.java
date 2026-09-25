package com.library.dto;

public record BookResponse(
    Long id,
    String title,
    String author,
    String genre,
    String isbn,
    Integer publishedYear,
    Integer pages) {}
