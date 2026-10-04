package com.library.dto;

import java.util.Map;

public record BookStatsResponse(
    int totalBooks,
    Double averagePages,
    Integer oldestPublicationYear,
    Integer newestPublicationYear,
    Map<String, Integer> genreCounts) {}
