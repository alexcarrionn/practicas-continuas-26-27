package com.library.dto;

public record BookSearchCriteria(
        String keyword,
        String author,
        String genre,
        Integer minYear,
        Integer maxYear
) {}
