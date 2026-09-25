package com.library.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record BookSearchResponse(
        List<BookResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static BookSearchResponse from(Page<BookResponse> result) {
        return new BookSearchResponse(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }
}
